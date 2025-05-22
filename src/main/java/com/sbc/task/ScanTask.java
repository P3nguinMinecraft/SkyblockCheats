package com.sbc.task;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.ArrayList;
import java.util.List;
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

        int minY = world.getBottomY();
        int maxY = world.getTopY();

        ChunkPos centerChunk = new ChunkPos(client.player.getBlockPos());
        int radius = client.options.getViewDistance().getValue();

        List<ChunkPos> spiralChunks = generateSpiral(centerChunk, radius);

        outerLoop:
        for (ChunkPos chunkPos : spiralChunks) {
            if (cancelled) break;

            WorldChunk chunk = client.world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
            if (chunk == null) continue;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        if (cancelled) break outerLoop;

                        BlockPos pos = chunkPos.getStartPos().add(x, y, z);
                        BlockState state = chunk.getBlockState(pos);

                        if (state.isAir()) continue;

                        if (blockPredicate.test(pos)) {
                            foundCallback.accept(pos);
                            done = true;
                            return;
                        }
                    }
                }
            }
        }

        done = true;
    }

    private List<ChunkPos> generateSpiral(ChunkPos center, int radius) {
        List<ChunkPos> result = new ArrayList<>();
        result.add(center);

        int dx = 0, dz = -1;
        int x = 0, z = 0;

        for (int layer = 1; layer <= radius * 2; layer++) {
            for (int i = 0; i < (layer < radius * 2 ? layer : radius * 2); i++) {
                if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                    result.add(new ChunkPos(center.x + x, center.z + z));
                }

                int temp = dx;
                dx = -dz;
                dz = temp;

                x += dx;
                z += dz;
            }
        }

        return result;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isDone() {
        return done;
    }
}
