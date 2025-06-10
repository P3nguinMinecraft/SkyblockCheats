package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.ServerHistory;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ServerHistoryClear {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)  {
		var command = ClientCommandManager.literal("serverhistory")
    		.then(ClientCommandManager.literal("clear")
                .executes(ctx -> {
                    ServerHistory.clearServerLog();
                    ChatUtils.addMessage("§2[SBC] §r§aServer History cleared.");
                    return 1;
                })
            );
		
		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
	}
}
