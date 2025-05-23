package com.sbc.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

public class ScanTask implements Runnable {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private volatile boolean cancelled = false;
    private volatile boolean done = false;
    private final int threadCount = 4;

    private final Consumer<BlockPos> foundCallback;
    private final Predicate<BlockPos> blockPredicate;

    public ScanTask(Consumer<BlockPos> foundCallback, Predicate<BlockPos> blockPredicate) {
        this.foundCallback = foundCallback;
        this.blockPredicate = blockPredicate;
    }

    public void start() {
        new Thread(this, "ScanTaskMasterThread").start();
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
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        int chunksPerThread = (int) Math.ceil((double) spiralChunks.size() / threadCount);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int start = i * chunksPerThread;
            int end = Math.min(start + chunksPerThread, spiralChunks.size());

            Runnable subTask = () -> {
                BlockPos.Mutable pos = new BlockPos.Mutable();
                for (int j = start; j < end && !cancelled && !done; j++) {
                    ChunkPos chunkPos = spiralChunks.get(j);
                    WorldChunk chunk = world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
                    if (chunk == null) continue;

                    for (int x = 0; x < 16 && !cancelled && !done; x++) {
                        for (int z = 0; z < 16 && !cancelled && !done; z++) {
                            for (int y = minY; y < maxY && !cancelled && !done; y++) {
                                pos.set(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
                                BlockState state = chunk.getBlockState(pos);
                                if (state.isAir()) continue;

                                if (blockPredicate.test(pos)) {
                                    client.execute(() -> foundCallback.accept(pos.toImmutable())); // ensure it's on main thread
                                    cancelled = true;
                                    done = true;
                                    return;
                                }
                            }
                        }
                    }
                }
            };

            threads.add(new Thread(subTask, "ScanWorker-" + i));
        }

        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }

        done = true;
        executor.shutdownNow();
    }

    private List<ChunkPos> generateSpiral(ChunkPos center, int radius) {
        List<ChunkPos> result = new ArrayList<>();
        int x = 0, z = 0;
        int dx = 0, dz = -1;

        int max = radius * 2 + 1;
        for (int i = 0; i < max * max; i++) {
            int cx = center.x + x;
            int cz = center.z + z;
            if (Math.abs(x) <= radius && Math.abs(z) <= radius) {
                result.add(new ChunkPos(cx, cz));
            }

            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }

            x += dx;
            z += dz;
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
