package com.sbc.mixin.sodium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sbc.feature.rift.timite.TimiteState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.world.LevelSlice")
public class LevelSliceMixin {
    @ModifyReturnValue(method = "getBlockState(III)Lnet/minecraft/block/BlockState;", at = @At("RETURN"))
    private BlockState redirectGetBlockState(BlockState original) {
        if (TimiteState.getTimite()) {
            if (original.isOf(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)) {
                return Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
            }
            if (original.isOf(Blocks.BLUE_STAINED_GLASS_PANE)) {
                return Blocks.BLUE_STAINED_GLASS.getDefaultState();
            }
            if (original.isOf(Blocks.PURPLE_STAINED_GLASS_PANE)) {
                return Blocks.PURPLE_STAINED_GLASS.getDefaultState();
            }
        }
        return original;
    }
}
