package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.TPSTracker;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ServerTPS {
	
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var command = ClientCommandManager.literal("tps")
			.executes(ctx -> {
			float tps = TPSTracker.getTPS();
			String color = tps > 19.0f ? "§a" : tps > 16.0f ? "§e" : "§c";
			ChatUtils.addMessage("§2[SBC] §r§3TPS: " + color + tps);
			return 1;
		});
        
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}