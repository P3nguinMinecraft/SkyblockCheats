package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class Uptime {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
        CommandRegistryAccess registryAccess) {
		dispatcher.register(ClientCommandManager.literal("sbc")
			.then(ClientCommandManager.literal("uptime")
				.executes(ctx -> {
					long uptime = MinecraftClient.getInstance().world.getTimeOfDay();
					ChatUtils.sendMessage("Uptime: Day " + Math.round(uptime / 24000 * 100.0) / 100.0);
					return 1;
				})
			)
		);
		dispatcher.register(ClientCommandManager.literal("skyblockcheats")
			.then(ClientCommandManager.literal("uptime")
				.executes(ctx -> {
					long uptime = MinecraftClient.getInstance().world.getTimeOfDay();
					ChatUtils.sendMessage("Uptime: Day " + Math.round(uptime / 24000 * 100.0) / 100.0);
					return 1;
				})
			)
		);
	}

}
