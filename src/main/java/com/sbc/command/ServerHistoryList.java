package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.feature.server.ServerHistory;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ServerHistoryList {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)  {
		var command = ClientCommandManager.literal("serverhistory")
    		.then(ClientCommandManager.literal("list")
                .executes(ctx -> {
                    String history = ServerHistory.getString();
                    ChatUtils.addMessage("§2[SBC] §r§dServer History: §r§7" + history);
                    return 1;
                })
            );
		
		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
	}
}
