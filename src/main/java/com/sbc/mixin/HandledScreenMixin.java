package com.sbc.mixin;

import com.sbc.feature.skyblock.AnvilHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Shadow
    private Slot focusedSlot;

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir){
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        if (focusedSlot == null) return;
        if (AnvilHelper.currentScreen == screen && focusedSlot.id == 0){
            AnvilHelper.registerClick();
            cir.cancel();
        }
    }
}
