package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.server.TPSTracker;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ServerTPS {
	
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var command = ClientCommandManager.literal("tps")
			.executes(ctx -> {
			float tps = TPSTracker.getTps();
			float mspt = TPSTracker.getMspt();
			String color = tps > 19.0f ? "§a" : tps > 16.0f ? "§e" : "§c";
			ChatUtils.sendFormattedMessage(Text.literal("§2[SBC] §r§3TPS: " + color + tps).setStyle(Style.EMPTY
			        .withHoverEvent(new HoverEvent.ShowText(Text.literal("MSPT: " + mspt + "ms")))
			    ));
			return 1;
		});
        
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}