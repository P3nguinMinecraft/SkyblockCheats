package com.sbc.feature.mining;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class PowderChest {
    public static final HashSet<BlockPos> chests = new HashSet<>();
    public static final HashSet<BlockPos> clicked = new HashSet<>();
    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(PowderChest::tick);
    }
    public static void tick(MinecraftClient client){
        if (client.player == null || client.world == null) return;
        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockhit)) return;
    }




}
