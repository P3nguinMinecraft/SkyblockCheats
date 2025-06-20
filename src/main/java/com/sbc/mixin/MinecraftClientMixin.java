package com.sbc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.sbc.accessor.IMinecraftClientAccessor;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)

public abstract class MinecraftClientMixin implements IMinecraftClientAccessor {
	@Invoker("doAttack")
	@Override
    public abstract boolean invokeAttack();

    @Invoker("doItemUse")
    @Override
    public abstract void invokeItemUse();   
}