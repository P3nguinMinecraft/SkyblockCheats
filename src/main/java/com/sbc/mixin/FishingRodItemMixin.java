package com.sbc.mixin;

import com.sbc.feature.fishing.FishingMacro;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    private static void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        FishingMacro.rodCast(world, user, hand);
    }
}
