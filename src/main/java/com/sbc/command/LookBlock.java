package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.sbc.feature.Camera;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class LookBlock {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		var command = ClientCommandManager.literal("look")
			.then(ClientCommandManager.literal("block")
				.then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
					.then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
						.then(ClientCommandManager.argument("z", IntegerArgumentType.integer())
							.executes(context -> {
								int x = IntegerArgumentType.getInteger(context, "x");
								int y = IntegerArgumentType.getInteger(context, "y");
								int z = IntegerArgumentType.getInteger(context, "z");
								
								ChatUtils.addMessage("§eLooking at block: §r" + x + ", " + y + ", " + z);
								Camera.lookBlock(x, y, z);
								
								return 1;
							})
						)
					)
				)
			);
		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
	}
}
