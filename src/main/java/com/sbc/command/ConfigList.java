package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.util.ChatUtils;
import com.sbc.util.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ConfigList {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
            .then(ClientCommandManager.literal("list")
                .executes(ctx -> {
                    for (String key : ConfigManager.getAllKeys()) {
                        Object value = ConfigManager.getConfig(key);
                        ChatUtils.sendMessage(key + ": " + value);
                    }
                    return 1;
                })
            )
        );
        dispatcher.register(ClientCommandManager.literal("skyblockcheats")
            .then(ClientCommandManager.literal("list")
                .executes(ctx -> {
                    for (String key : ConfigManager.getAllKeys()) {
                        Object value = ConfigManager.getConfig(key);
                        ChatUtils.sendMessage(key + ": " + value);
                    }
                    return 1;
                })
            )
        );
    }
}
