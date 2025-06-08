package com.sbc.feature;

import com.sbc.util.Config;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class GhostBlock {
	private static boolean keyPressed = false;
	
    public static void init() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!keyPressed || !(boolean) Config.getConfig("ghostblock")) return ActionResult.PASS;

            if (world.isClient) {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 19);
                });
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
    
    public static void handleKeyPress(boolean pressed) {
    	keyPressed = pressed;
    }
}
