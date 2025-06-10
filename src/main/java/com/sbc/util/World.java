package com.sbc.util;

import com.sbc.feature.GrottoSearchManager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkStatus;

public class World {
	private static ClientWorld lastWorld = null;
		public static void init() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client.world != lastWorld) {
	                lastWorld = client.world;
	                if (client.world != null) {
	                    onServerSwitch();
	                }
	            }
	        });
		}
	
	public static void waitLoaded(long timeoutMs) throws InterruptedException {
	    MinecraftClient client = MinecraftClient.getInstance();
	    if (client.world == null || client.player == null) return;

	    int radiusChunks = client.options.getViewDistance().getValue();
	    BlockPos playerPos = client.player.getBlockPos();
	    long deadline = System.currentTimeMillis() + timeoutMs;

	    while (System.currentTimeMillis() < deadline) {
	        boolean allLoaded = true;

	        outer:
	        for (int dx = -radiusChunks * 16; dx <= radiusChunks * 16; dx += 16) {
	            for (int dz = -radiusChunks * 16; dz <= radiusChunks * 16; dz += 16) {
	                BlockPos checkPos = playerPos.add(dx, 0, dz);
	                if (client.world.getChunk(checkPos.getX() >> 4, checkPos.getZ() >> 4, ChunkStatus.FULL, false) == null) {
	                    allLoaded = false;
	                    break outer;
	                }
	            }
	        }

	        if (allLoaded) return;
	        Thread.sleep(50);
	    }
	}
	
	public static void onServerSwitch() {
		GrottoSearchManager.clearSearch();
		GrottoSearchManager.endScanTasks();
	}
}
