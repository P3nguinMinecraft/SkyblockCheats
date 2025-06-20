package com.sbc.mixin;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbc.feature.TPSTracker;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onWorldTimeUpdate", at = @At("RETURN"))
	private void onTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
	    TPSTracker.update(packet.time());
	}
}
