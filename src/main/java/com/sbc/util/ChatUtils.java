package com.sbc.util;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ChatUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
	private static ArrayList<QueueItem> queue = new ArrayList<>();
    private static volatile String lastGameMessage = "";
    private static ArrayList<String> lastGameMessages = new ArrayList<>();

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            onGameMessage(message.getString());
        });
    }
    
    private static void onGameMessage(String message) {
		lastGameMessage = message;

		for (QueueItem item : queue) {
			if (item.isMatch() && message.equals(item.getMessage())) {
				item.complete();
				queue.remove(item);
				break;
			}
			else if (!item.isMatch() && message.contains(item.getMessage())) {
				item.complete();
				queue.remove(item);
				break;
			}
		}
	}

	public static String getLastGameMessage() {
		return lastGameMessage;
	}
	
	public static ArrayList<String> getLastGameMessages() {
		return lastGameMessages;
	}

    public static void waitForChatMessage(String message, boolean match, boolean delay, long timeoutMs, Runnable completion, Runnable onTimeout) {
        QueueItem item = new QueueItem(message, match, System.currentTimeMillis() + timeoutMs, completion);
        queue.add(item);
        if (!delay) return;
        while (queue.contains(item)) {
			try {
				Thread.sleep(10);
				if (!item.valid()) {
					queue.remove(item);
					onTimeout.run();
					return;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
    }

    public static void sendMessage(Object msg) {
    	String message = msg.toString();
        client.inGameHud.getChatHud().addMessage(Text.literal(message));
    }
    
    public static void sendServerMessage(Object msg) {
    	if (client == null || client.inGameHud == null || client.player == null) return;
        client.execute(() -> client.player.networkHandler.sendChatMessage(msg.toString()));
	}
    
    public static void sendActionBar(Object msg) {
    	if (client == null || client.inGameHud == null) return;
		client.inGameHud.setOverlayMessage(Text.literal(msg.toString()), false);
	}
    
    public static void sendFormattedMessage(Text... components) {
        if (client == null || client.inGameHud == null) return;
        
        MutableText fullMessage = Text.empty();
        for (Text component : components) {
            fullMessage = fullMessage.append(component);
        }

        client.inGameHud.getChatHud().addMessage(fullMessage);
    }
}

class QueueItem {
	private String message;
	private boolean match;
	private long end;
	private Runnable completion;
	
	public QueueItem(String message, boolean match, long end, Runnable completion) {
		this.message = message;
		this.match = match;
		this.end = end;
		this.completion = completion;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isMatch() {
		return match;
	}
	
	public boolean valid() {
		return System.currentTimeMillis() < end;
	}
	
	public void complete() {
		completion.run();
	}
}