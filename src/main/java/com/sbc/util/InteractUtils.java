package com.sbc.util;

import java.util.function.Consumer;

import com.sbc.accessor.IMinecraftClientAccessor;

import net.minecraft.client.MinecraftClient;

public class InteractUtils {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private static final Consumer<MinecraftClient> lClick = (mc) -> ((IMinecraftClientAccessor) mc).invokeAttack();
	private static final Consumer<MinecraftClient> rClick = (mc) -> ((IMinecraftClientAccessor) mc).invokeItemUse();

	public static void leftClick() {
    	lClick.accept(MinecraftClient.getInstance());
    }

    public static void rightClick() {
    	rClick.accept(MinecraftClient.getInstance());
    }

    public static void jump() {
    	if (client.player != null) {
    	    client.player.jump();
    	}
    }
    
    public static void sneak() {
    	KeyboardUtils.sneaking = !KeyboardUtils.sneaking;
    }
}
