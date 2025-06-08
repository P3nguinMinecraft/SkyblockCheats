package com.sbc.feature;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TPSTracker {
	private static MinecraftClient client = MinecraftClient.getInstance();
	private static ArrayList<Long> ticks = new ArrayList<>();
	private static ArrayList<Long> times = new ArrayList<>();
	
	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
		    if (client.world != null) {
		        update();
		    }
		});
	}
	
	public static void update() {
		long currentTime = System.nanoTime();
		long worldTime = client.world.getTime();
		if (ticks.size() > 20) {
			ticks.remove(0);
			times.remove(0);
		}
		ticks.add(worldTime);
		times.add(currentTime);
	}
	
	public static float getTPS() {
		if (ticks.size() < 2) return 20.0f;
		long dTick = ticks.get(ticks.size() - 1) - ticks.get(0);
		long dNT = times.get(times.size() - 1) - times.get(0);
		float raw = dTick / (dNT / 1000000000.0f);
		float tps = Math.round(raw * 10f) / 10f;
		return Math.min(tps, 20.0f);
	}
	
}
