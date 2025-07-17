package com.sbc.feature;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class ServerHistory {
	private static final ArrayList<String> serverList = new ArrayList<>();
	private static final LinkedHashMap<String, Long> serverLog = new LinkedHashMap<>();
	
	public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
        	String msg = message.getString();
            if (msg.startsWith("Sending to server ")) {
            	String id = msg.substring("Sending to server ".length()).replaceAll("\\.", "");
            	logServer(id);
            }
        });
    }
	
	private static void logServer(String id) {
		if (serverLog.containsKey(id)) {
			int maxLogTime = (int) Config.getConfig("max-log-time");
			int lastTime = (int) (System.currentTimeMillis() - serverLog.get(id)) / 1000;
			serverLog.remove(id);
			if (maxLogTime <= 0 || lastTime < maxLogTime)
				ChatUtils.addMessage("§2[SBC] §r§aYou were in server §7" + id + " §e" + lastTime + " seconds ago.");
		}
		serverLog.put(id, System.currentTimeMillis());
		serverList.add(id);
	}
	
	public static void clearServerLog() {
		serverLog.clear();
		serverList.clear();
	}
	
	public static LinkedHashMap<String, Long> getLog() {
		return serverLog;
	}
	
	public static String getString() {
		return serverList.toString();
	}
}
