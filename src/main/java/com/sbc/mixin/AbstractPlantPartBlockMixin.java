package com.sbc.mixin;

import com.sbc.util.Config;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractPlantPartBlock.class)
public class AbstractPlantPartBlockMixin {
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void modifyHitboxOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Block block = state.getBlock();
        // No hitbox
        if ((block instanceof KelpBlock || block instanceof KelpPlantBlock) && Boolean.TRUE.equals(Config.getConfig("lumie-hitboxes")))
            cir.setReturnValue(VoxelShapes.empty());
    }
}
