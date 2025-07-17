package com.sbc.mixin;

import com.sbc.command.Render;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends Entity> void cancelEntityRender(
            E entity,
            double x, double y, double z,
            float tickProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        for (String key : Render.toggles.keySet()) {
            if (Render.toggles.get(key)) continue;

            Class<?> clazz = Render.classes.get(key);
            if (clazz != null && clazz.isInstance(entity)) {
                // Don't cancel rendering the client player
                if (entity == MinecraftClient.getInstance().player) continue;
                ci.cancel();
            }
        }
    }

}
