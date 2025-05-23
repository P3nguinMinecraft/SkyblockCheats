package com.sbc.util;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtils {
    private static volatile String lastGameMessage = "";

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            lastGameMessage = message != null ? message.getString() : "";
            System.out.println(lastGameMessage);
        });
    }

    public static boolean waitForChatMessage(String contains, long timeoutMs) {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            if (lastGameMessage.contains(contains)) {
                return true;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        return false;
    }

    public static void sendMessage(Object msg) {
    	String message = msg.toString();
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(Text.literal(message));
    }
}
