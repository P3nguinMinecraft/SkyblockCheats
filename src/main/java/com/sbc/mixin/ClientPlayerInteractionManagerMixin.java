package com.sbc.mixin;

import com.sbc.util.Config;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    private int blockBreakingCooldown;
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true) // MCP: onPlayerDamageBlock
    private void blockBreakingProgress(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(Config.getConfig("disable-breaking-cooldown")))
            this.blockBreakingCooldown = 0;
    }

}
