package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ConfigRemove {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
    		.then(ClientCommandManager.literal("config")
	            .then(ClientCommandManager.literal("remove")
	                .then(ClientCommandManager.argument("key", StringArgumentType.word())
	                    .suggests((ctx, builder) -> {
	                        for (String key : Config.getAllKeys()) {
	                            builder.suggest(key);
	                        }
	                        return builder.buildFuture();
	                    })
	                    .executes(ctx -> {
	                        String key = StringArgumentType.getString(ctx, "key");
	                        Config.removeConfig(key);
	                        ChatUtils.sendMessage("Removed config key: " + key);
	                        return 1;
	                    })
	                )
	            )
            )
        );
        dispatcher.register(ClientCommandManager.literal("skyblockcheats")
    		.then(ClientCommandManager.literal("config")
	            .then(ClientCommandManager.literal("remove")
	                .then(ClientCommandManager.argument("key", StringArgumentType.word())
	                    .suggests((ctx, builder) -> {
	                        for (String key : Config.getAllKeys()) {
	                            builder.suggest(key);
	                        }
	                        return builder.buildFuture();
	                    })
	                    .executes(ctx -> {
	                        String key = StringArgumentType.getString(ctx, "key");
	                        Config.removeConfig(key);
	                        ChatUtils.sendMessage("Removed config key: " + key);
	                        return 1;
	                    })
	                )
	            )
            )
        );
    }
}
