package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ConfigSet {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
    		.then(ClientCommandManager.literal("config")
	            .then(ClientCommandManager.literal("set")
	                .then(ClientCommandManager.argument("key", StringArgumentType.word())
	                    .suggests((ctx, builder) -> {
	                        for (String key : Config.getAllKeys()) {
	                            builder.suggest(key);
	                        }
	                        return builder.buildFuture();
	                    })
	                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
	                        .executes(ctx -> {
	                            String key = StringArgumentType.getString(ctx, "key");
	                            String valueStr = StringArgumentType.getString(ctx, "value");

	                            if (!Config.isValidKey(key)) {
	                                ChatUtils.sendMessage("§cInvalid config key: " + key);
	                                return 0;
	                            }

	                            Object currentValue = Config.getConfig(key);
	                            try {
	                                if (currentValue instanceof Integer) {
	                                    int parsedValue = Integer.parseInt(valueStr);
	                                    if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                }
	                                else if (currentValue instanceof Float) {
										float parsedValue = Float.parseFloat(valueStr);
										if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
									} else if (currentValue instanceof Double) {
										double parsedValue = Double.parseDouble(valueStr);
										if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                } else if (currentValue instanceof Boolean) {
	                                    boolean parsedValue = Boolean.parseBoolean(valueStr);
	                                    if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                } else {
	                                    if (Config.setConfig(key, valueStr)) {
											ChatUtils.sendMessage("§eSet " + key + " to \"" + valueStr + "\" (as string)");
										}
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
            )
        );
        dispatcher.register(ClientCommandManager.literal("skyblockcheats")
    		.then(ClientCommandManager.literal("config")
	            .then(ClientCommandManager.literal("set")
	                .then(ClientCommandManager.argument("key", StringArgumentType.word())
	                    .suggests((ctx, builder) -> {
	                        for (String key : Config.getAllKeys()) {
	                            builder.suggest(key);
	                        }
	                        return builder.buildFuture();
	                    })
	                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
	                        .executes(ctx -> {
	                            String key = StringArgumentType.getString(ctx, "key");
	                            String valueStr = StringArgumentType.getString(ctx, "value");

	                            if (!Config.isValidKey(key)) {
	                                ChatUtils.sendMessage("§cInvalid config key: " + key);
	                                return 0;
	                            }

	                            Object currentValue = Config.getConfig(key);
	                            try {
	                                if (currentValue instanceof Integer) {
	                                    int parsedValue = Integer.parseInt(valueStr);
	                                    if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                }
	                                else if (currentValue instanceof Float) {
										float parsedValue = Float.parseFloat(valueStr);
										if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
									} else if (currentValue instanceof Double) {
										double parsedValue = Double.parseDouble(valueStr);
										if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                } else if (currentValue instanceof Boolean) {
	                                    boolean parsedValue = Boolean.parseBoolean(valueStr);
	                                    if (Config.setConfig(key, parsedValue)) {
											ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
										}
	                                } else {
	                                    if (Config.setConfig(key, valueStr)) {
											ChatUtils.sendMessage("§eSet " + key + " to \"" + valueStr + "\" (as string)");
										}
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
            )
        );
    }
}
