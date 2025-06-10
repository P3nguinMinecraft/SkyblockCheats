package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ConfigSet {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.literal("config")
            .then(ClientCommandManager.literal("set")
                .then(ClientCommandManager.argument("key", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (String key : Config.getAllKeys()) {
                            builder.suggest(key);
                        }
                        return builder.buildFuture();
                    })
                    .then(ClientCommandManager.argument("value", StringArgumentType.string())
                		.suggests((ctx, builder) -> {
                			String arg = ctx.getArgument("key", String.class);
                			if (arg.equals("sound")) {
	        		            for (Identifier id : Registries.SOUND_EVENT.getIds()) {
	        		                builder.suggest(id.toString());
	        		            }
                			}
                			else if (arg.equals("solid-highlight") || arg.equals("ping-on-found") || arg.equals("filter-Y")
        						|| arg.equals("auto-melody") || arg.equals("ghost-block")) {
                				builder.suggest("true").suggest("false");
                			}
        		            return builder.buildFuture();
        		        })
                        .executes(ctx -> {
                            String key = StringArgumentType.getString(ctx, "key");
                            String valueStr = StringArgumentType.getString(ctx, "value");

                            if (!Config.isValid(key)) {
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
								}
                                else if (currentValue instanceof Double) {
									double parsedValue = Double.parseDouble(valueStr);
									if (Config.setConfig(key, parsedValue)) {
										ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
									}
                                }
                                else if (currentValue instanceof Boolean) {
                                    boolean parsedValue = Boolean.parseBoolean(valueStr);
                                    if (Config.setConfig(key, parsedValue)) {
										ChatUtils.sendMessage("§eSet " + key + " to " + parsedValue);
									}
                                }
                                else {
                                    if (Config.setConfig(key, valueStr)) {
										ChatUtils.sendMessage("§eSet " + key + " to \"" + valueStr + "\" (as string)");
									}
                                }
                            }
                            catch (Exception e) {
                                ChatUtils.sendMessage("§cFailed to set " + key + ": " + e.getMessage());
                                return 0;
                            }

                            return 1;
                        })
                    )
                )
			);           
	            
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}
