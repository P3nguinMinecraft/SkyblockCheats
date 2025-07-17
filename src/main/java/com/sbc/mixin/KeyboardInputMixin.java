package com.sbc.mixin;


import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbc.util.KeyboardUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
	private static final MinecraftClient client = MinecraftClient.getInstance();
    @Shadow @Final private GameOptions settings;

    @Inject(method = "tick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/input/KeyboardInput;playerInput:Lnet/minecraft/util/PlayerInput;",
            ordinal = 0,
            shift = Shift.AFTER,
            opcode = Opcodes.PUTFIELD))
    private void customMovement(CallbackInfo ci) {
        if (client.currentScreen == null){
            this.playerInput = new PlayerInput(
                    KeyboardUtils.walkForward || this.settings.forwardKey.isPressed(),
                    KeyboardUtils.walkBack || this.settings.backKey.isPressed(),
                    KeyboardUtils.walkLeft || this.settings.leftKey.isPressed(),
                    KeyboardUtils.walkRight || this.settings.rightKey.isPressed(),
                    this.settings.jumpKey.isPressed(),
                    KeyboardUtils.sneaking || this.settings.sneakKey.isPressed(),
                    this.settings.sprintKey.isPressed()
            );
        }
    }
}
