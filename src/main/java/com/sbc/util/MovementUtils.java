package com.sbc.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class MovementUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static double getFriction(){
        BlockPos below = client.player.getBlockPos().down();
        BlockState state = client.world.getBlockState(below);

        double slipperiness = state.getBlock().getSlipperiness();
        double friction = client.player.isOnGround() ? slipperiness * 0.91 : 0.91;
        return friction;
    }

    public static double getStopDistance() {
        double distance = 0;
        double speed = client.player.getVelocity().horizontalLength();

        while (speed > 0.003) {
            distance += speed;
            speed *= getFriction();
        }

        return distance;
    }

}
