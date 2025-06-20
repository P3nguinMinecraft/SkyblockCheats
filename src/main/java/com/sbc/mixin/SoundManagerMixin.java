package com.sbc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbc.util.ListenerManager;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    private void onPlaySound(SoundInstance sound, CallbackInfo ci) {
        if (sound == null) return;
        ListenerManager.onSound(sound);
    }
}
