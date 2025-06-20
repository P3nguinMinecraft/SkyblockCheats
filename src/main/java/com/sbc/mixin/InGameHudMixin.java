package com.sbc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbc.util.ListenerManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private Text title;
    @Shadow private Text subtitle;
    @Shadow private int titleRemainTicks;
    @Shadow private Text overlayMessage;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (title != null && titleRemainTicks > 0) {
            ListenerManager.onTitle(title);
        }

        if (subtitle != null && titleRemainTicks > 0) {
            ListenerManager.onSubtitle(subtitle);
        }

        if (overlayMessage != null){
            ListenerManager.onOverlay(overlayMessage);
        }
    }
}