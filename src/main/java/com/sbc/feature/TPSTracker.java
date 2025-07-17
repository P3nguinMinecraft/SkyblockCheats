package com.sbc.feature;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TPSTracker {
	private static final MinecraftClient client = MinecraftClient.getInstance();
    private static long lastTick = -1;
    private static long lastNano = -1;
    private static double mspt = -1f;
    private static double tps = -1f;
    
    private static final ArrayList<Double> msptList = new ArrayList<>();
    
    private static boolean integrated = false;
    
    public static void init() {
		if (client.getServer() != null) {
			integrated = true;
	        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
	            long[] ticks = mc.getServer().getTickTimes();
	            int count = Math.min(ticks.length, 100);
	            long sum = 0;
	            for (int i = ticks.length - count; i < ticks.length; i++) {
	                sum += ticks[i];
	            }
	            mspt = (double) sum / count / 1000000D;
	            tps = mspt <= 50 ? 20D : (1000D / mspt);
	        });
		}
	}

    public static void update(long serverTick) {
    	if (integrated) return;
        long now = System.nanoTime();

        if (lastTick != -1 && lastNano != -1) {
            long tickDelta = serverTick - lastTick;
            if (tickDelta > 0) {
            	double c_mspt = ((double) (now - lastNano) / (double) tickDelta) / 1000000D;
                msptList.add(c_mspt);
				if (msptList.size() > 3) {
					msptList.remove(0);
				}
				
				mspt = msptList.stream().mapToDouble(Double::doubleValue).average().orElse(50.0);
		        tps = mspt <= 50 ? 20D : (1000D / mspt);
            }
        }
        lastTick = serverTick;
        lastNano = now;
    }

    public static float getTps() {
        return (float) Math.round(tps * 100) / 100f;
    }
    
    public static float getMspt() {
		return (float) Math.round(mspt * 100) / 100f;
	}

	public static boolean isIntegrated() {
		return integrated;
	}
}