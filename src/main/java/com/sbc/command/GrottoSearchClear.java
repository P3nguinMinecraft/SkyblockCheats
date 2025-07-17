package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.mining.GrottoSearchManager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class GrottoSearchClear {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.literal("gsearch")
			.then(ClientCommandManager.literal("clear")
				.executes(ctx -> {
					GrottoSearchManager.clearSearch();
					GrottoSearchManager.endTasks();
					return 1;
				})
			);
		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}