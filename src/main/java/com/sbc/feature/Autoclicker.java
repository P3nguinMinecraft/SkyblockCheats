package com.sbc.feature;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.InteractUtils;

import net.minecraft.client.MinecraftClient;

public class Autoclicker {
	private static boolean autoLeftClickEnabled = false;
	private static boolean autoRightClickEnabled = false;
	private static final MinecraftClient client = MinecraftClient.getInstance();
	
	public static void init() {
		new Thread(() -> {
		    while (true) {
		    	if (autoLeftClickEnabled && client.currentScreen == null) {
			        client.execute(() -> {
			        	InteractUtils.leftClick();
			        });
		    	}
		        try {
		            Thread.sleep((int)(1000.0f / (float) Config.getConfig("left-click-cps")));
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		}).start();
		
		new Thread(() -> {
		    while (true) {
		    	if (autoRightClickEnabled && client.currentScreen == null) {
			        client.execute(() -> {
			        	InteractUtils.rightClick();
			        });
		    	}
		        try {
		            Thread.sleep((int)(1000.0f / (float) Config.getConfig("right-click-cps")));
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		}).start();
	}
	
	public static void toggleAutoLeftClick() {
		autoLeftClickEnabled = !autoLeftClickEnabled;
		if (autoLeftClickEnabled) {
			ChatUtils.addMessage("§2[SBC] §r§aLeft Click Enabled");
		} else {
			ChatUtils.addMessage("§2[SBC] §r§cLeft Click Disabled");
		}
	}
	public static void toggleAutoRightClick() {
		autoRightClickEnabled = !autoRightClickEnabled;
		if (autoRightClickEnabled) {
			ChatUtils.addMessage("§2[SBC] §r§aRight Click Enabled");
		} else {
			ChatUtils.addMessage("§2[SBC] §r§cRight Click Disabled");
		}
	}
}
