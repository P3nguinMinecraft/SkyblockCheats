package com.sbc.mixin;

import com.sbc.feature.mining.PowderChest;
import com.sbc.util.Config;
import com.sbc.util.Skyblock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void modifyChestOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (Boolean.TRUE.equals(Config.getConfig("powder-hitboxes")) && Skyblock.inCH()) {
            if (state.isOf(Blocks.CHEST)){
                if (PowderChest.clicked.contains(pos)){
                    cir.setReturnValue(VoxelShapes.fullCube());
                } else {
                    cir.setReturnValue(VoxelShapes.empty());
                }
            }
        }
    }
}
