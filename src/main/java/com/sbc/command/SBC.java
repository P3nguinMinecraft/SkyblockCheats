package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class SBC {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc").executes(ctx -> {
            ChatUtils.addMessage("Base command");
            return 1;
        }));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").executes(ctx -> {
            ChatUtils.addMessage("Base command");
            return 1;
        }));
    }
}
