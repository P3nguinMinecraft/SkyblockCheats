package com.sbc.command;

import java.util.HashMap;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.object.UniqueAction;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Action {
	private static HashMap<String, UniqueAction> actions = new HashMap<>();
    public static void add(UUID id, Runnable action) {
    	UniqueAction uniqueAction = new UniqueAction(action, id);
    	actions.put(id.toString(), uniqueAction);
    }
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.literal("action")
            .then(ClientCommandManager.argument("uuid", StringArgumentType.word())
                .executes(context -> {
                	String id = StringArgumentType.getString(context, "uuid");
                	if (actions.containsKey(id)) {
						actions.get(id).run();
					}
                	else {
                		ChatUtils.addMessage("§cNo action found. §r§7Do not try to run this command manually, it is intended to be used for §r§aclicks!");
                	}
                    return 1;
                })
            );
    	
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}
