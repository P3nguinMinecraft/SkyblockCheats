package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ConfigSet {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
            .then(ClientCommandManager.literal("set")
                .then(ClientCommandManager.argument("key", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (String key : ConfigManager.getAllKeys()) {
                            builder.suggest(key);
                        }
                        return builder.buildFuture();
                    })
                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
                        .executes(ctx -> {
                            String key = StringArgumentType.getString(ctx, "key");
                            String valueStr = StringArgumentType.getString(ctx, "value");

                            if (!ConfigManager.isValidKey(key)) {
                                ChatUtils.sendMessage("§cInvalid config key: " + key);
                                return 0;
                            }

                            Object currentValue = ConfigManager.getConfig(key);
                            try {
                                if (currentValue instanceof Integer) {
                                    int parsedValue = Integer.parseInt(valueStr);
                                    ChatUtils.sendMessage("Set " + key + " to " + parsedValue);
                                    ConfigManager.setConfig(key, parsedValue);
                                } else if (currentValue instanceof Boolean) {
                                    boolean parsedValue = Boolean.parseBoolean(valueStr);
                                    ConfigManager.setConfig(key, parsedValue);
                                    ChatUtils.sendMessage("Set " + key + " to " + parsedValue);
                                } else {
                                    ConfigManager.setConfig(key, valueStr);
                                    ChatUtils.sendMessage("Set " + key + " to \"" + valueStr + "\" (as string)");
                                }
                            } catch (Exception e) {
                                ChatUtils.sendMessage("§cFailed to set " + key + ": " + e.getMessage());
                                return 0;
                            }

                            return 1;
                        })
                    )
                )
            )
        );
    }
}
