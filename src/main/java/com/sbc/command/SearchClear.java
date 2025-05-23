package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.SearchManager;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class SearchClear {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                              CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
        		.then(ClientCommandManager.literal("search")
        				.then(ClientCommandManager.literal("clear")
        						.executes(ctx -> {
        							SearchManager.clearSearch();
        							return 1;
        						})
        				)
        		)
        );
        dispatcher.register(ClientCommandManager.literal("skyblockcheats")
        		.then(ClientCommandManager.literal("search")
        				.then(ClientCommandManager.literal("clear")
        						.executes(ctx -> {
        							SearchManager.clearSearch();
        							return 1;
        						})
        				)
        		)
        );
    }
}