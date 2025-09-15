package com.sbc.mixin;

import com.sbc.feature.hunting.Lasso;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
    private MinecraftClient client;
    @Shadow
    private int blockBreakingCooldown;
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void disableCooldown(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(Config.getConfig("disable-break-cooldown"))) {
            this.blockBreakingCooldown = 0;
        }
        if (Boolean.TRUE.equals(Config.getConfig("auto-open-powder"))){
            if (client.world.getBlockState(pos).isOf(Blocks.CHEST)){
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private void attackingBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(Config.getConfig("anti-spleef"))){
            if (client.player == null) return;
            ClientPlayerEntity plr = client.player;
            if (pos.getY() >= plr.getY()) return;
            if (pos.getX() + 1 >= plr.getX() && pos.getX() < plr.getX() && pos.getZ() + 1 >= plr.getZ() && pos.getZ() < plr.getZ()) return;
            cir.cancel();
            return;
        }
        if (Boolean.TRUE.equals(Config.getConfig("auto-open-powder"))){
            if (client.world.getBlockState(pos).isOf(Blocks.CHEST)){
                cir.cancel();
            }
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if (Boolean.TRUE.equals(Config.getConfig("lasso-blockreel")) && Lasso.blockClick){
            cir.cancel();
        }
        else {
            Lasso.clicked();
        }
    }
}
