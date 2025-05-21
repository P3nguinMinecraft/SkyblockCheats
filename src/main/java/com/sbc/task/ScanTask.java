package com.sbc.task;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ScanTask implements Runnable {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private volatile boolean cancelled = false;
    private volatile boolean done = false;

    private final Consumer<BlockPos> foundCallback;
    private final Predicate<BlockPos> blockPredicate;

    public ScanTask(Consumer<BlockPos> foundCallback, Predicate<BlockPos> blockPredicate) {
        this.foundCallback = foundCallback;
        this.blockPredicate = blockPredicate;
    }

    public void start() {
        new Thread(this, "ScanTaskThread").start();
    }

    @Override
    public void run() {
        ClientWorld world = client.world;
        if (world == null || client.player == null) {
            done = true;
            return;
        }

        BlockPos playerPos = client.player.getBlockPos();
        int renderDistance = client.options.getViewDistance().getValue();
        int chunkRadius = renderDistance;
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(playerPos);
        visited.add(playerPos);

        while (!queue.isEmpty()) {
            if (cancelled) break;
            BlockPos pos = queue.poll();

            if (!isInLoadedChunks(pos, chunkX, chunkZ, chunkRadius)) continue;
            if (pos.getY() < world.getBottomY() || pos.getY() >= world.getTopY()) continue;

            if (blockPredicate.test(pos)) {
                foundCallback.accept(pos);
                break;
            }

            // Add neighbors (6 directions)
            for (BlockPos neighbor : Arrays.asList(
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west(),
                pos.up(),
                pos.down()
            )) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        done = true;
    }

    private boolean isInLoadedChunks(BlockPos pos, int centerChunkX, int centerChunkZ, int chunkRadius) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        return Math.abs(cx - centerChunkX) <= chunkRadius && Math.abs(cz - centerChunkZ) <= chunkRadius;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isDone() {
        return done;
    }
}
