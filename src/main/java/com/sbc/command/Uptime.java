package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.ServerUptime;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Uptime {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		var command = ClientCommandManager.literal("uptime")
			.executes(ctx -> {
				ServerUptime.displayUptime();
				return 1;
			});
		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
	}

}
