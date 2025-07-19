package com.sbc.feature.server;

import com.sbc.util.ChatUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ServerUptime {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	
	public static void displayUptime() {
		long uptime = client.world.getTimeOfDay();
		double days = Math.round(uptime / 24000.0 * 100.0) / 100.0;
		double hours = Math.round(uptime / 72000.0 * 100.0) / 100.0;
		double minutes = Math.round(uptime / 1200.0 * 100.0) / 100.0;
		ChatUtils.sendFormattedMessage(
        	Text.literal("§2[SBC] §r§pUptime: §r§dDay " + (int) Math.floor(days)).setStyle(Style.EMPTY),
        	Text.literal(" §r§7[Hover for details]").setStyle(Style.EMPTY
        		.withHoverEvent(new HoverEvent.ShowText(Text.literal("§6Or: §r§c" + days + " days | " + hours + " hours | " + minutes + " minutes | " + uptime + " ticks")))
        	)
		);
	}
}
