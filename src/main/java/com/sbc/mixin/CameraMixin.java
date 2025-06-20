package com.sbc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbc.accessor.ICameraAccessor;
import com.sbc.util.CameraUtils;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICameraAccessor {
	@Invoker("setRotation")
	@Override
	public abstract void invokeSetRotation(float yaw, float pitch);

	@Inject(method = "update", at = @At("RETURN"))
	private void onUpdateStart(BlockView area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		if (CameraUtils.freeze) {
			invokeSetRotation(CameraUtils.fyaw, CameraUtils.fpitch);
		}
	}
}

