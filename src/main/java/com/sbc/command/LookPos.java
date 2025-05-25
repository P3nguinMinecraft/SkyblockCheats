package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.sbc.feature.Camera;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class LookPos {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(ClientCommandManager.literal("sbc")
			.then(ClientCommandManager.literal("look")
				.then(ClientCommandManager.literal("pos")
					.then(ClientCommandManager.argument("x", FloatArgumentType.floatArg())
						.then(ClientCommandManager.argument("y", FloatArgumentType.floatArg())
							.then(ClientCommandManager.argument("z", FloatArgumentType.floatArg())
								.executes(context -> {
									float x = FloatArgumentType.getFloat(context, "x");
									float y = FloatArgumentType.getFloat(context, "y");
									float z = FloatArgumentType.getFloat(context, "z");
									
									ChatUtils.sendMessage("§eLooking at: §r" + x + ", " + y + ", " + z);
									Camera.lookPos(x, y, z);
									
									return 1;
								})
							)
						)
					)
				)
			)
		);
		dispatcher.register(ClientCommandManager.literal("skyblockcheats")
				.then(ClientCommandManager.literal("look")
					.then(ClientCommandManager.literal("pos")
						.then(ClientCommandManager.argument("x", FloatArgumentType.floatArg())
							.then(ClientCommandManager.argument("y", FloatArgumentType.floatArg())
								.then(ClientCommandManager.argument("z", FloatArgumentType.floatArg())
									.executes(context -> {
										float x = FloatArgumentType.getFloat(context, "x");
										float y = FloatArgumentType.getFloat(context, "y");
										float z = FloatArgumentType.getFloat(context, "z");
										
										ChatUtils.sendMessage("§eLooking at: §r" + x + ", " + y + ", " + z);
										Camera.lookPos(x, y, z);
										
										return 1;
									})
								)
							)
						)
					)
				)
			);
	}
}
