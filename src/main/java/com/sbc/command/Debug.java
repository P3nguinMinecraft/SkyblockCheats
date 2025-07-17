package com.sbc.command;

import java.util.HashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Debug {
	private static final HashMap<String, Runnable> debugCommands = new HashMap<>();
	
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.argument("key", StringArgumentType.greedyString())
            .executes(ctx -> {
            	String input = ctx.getArgument("key", String.class);
            	if (!input.startsWith("debug ")) return 0;
            	String key = input.substring(6);
                if (debugCommands.containsKey(key)) {
					debugCommands.get(key).run();
				} else {
					ChatUtils.addMessage("§cNo debug command found for: " + key);
				}
                return 1;
            });
    	
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
    
    public static void addCommand(String command, Runnable action) {
		debugCommands.put(command, action);
	}
}
