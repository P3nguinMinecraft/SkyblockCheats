package com.sbc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class SoundUtils {
    public static void playSound(float volume, float pitch, SoundEvent sound) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.world != null) {
            Vec3d pos = client.player.getPos();
            client.world.playSound(
                pos.x, pos.y, pos.z,
                sound,
                SoundCategory.PLAYERS,
                volume,
                pitch,
                false
            );
        }
    }
}
