package com.sbc.mixin;

import com.sbc.util.Config;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "playSound(Lnet/minecraft/entity/Entity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V", at = @At("HEAD"), cancellable = true)
    private void cancelSourceSound(@Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
        if (sound.matchesId(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE.id())) {
            if (Boolean.TRUE.equals(Config.getConfig("block-blank-hit-sound"))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At("HEAD"), cancellable = true)
    private void cancelServerSound(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch, boolean useDistance, long seed, CallbackInfo ci){
        if (event.id().equals(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE.id()) || event.id().equals(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK.id())) {
            if (Boolean.TRUE.equals(Config.getConfig("block-blank-hit-sound"))) {
                ci.cancel();
            }
        }
    }
}
