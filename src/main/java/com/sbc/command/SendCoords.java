package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sbc.util.ChatUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class SendCoords {
	MinecraftClient client = MinecraftClient.getInstance();
	
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var command = ClientCommandManager.literal("sendcoords")
			.executes(ctx -> {
				int x = (int) MinecraftClient.getInstance().player.getX();
				int y = (int) MinecraftClient.getInstance().player.getY();
				int z = (int) MinecraftClient.getInstance().player.getZ();
				ChatUtils.sendServerMessage(String.format("x: %d y: %d z: %d", x, y, z));
				return 1;
			});
        		
		dispatcher.register(ClientCommandManager.literal("sbc").then(command));
		dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}