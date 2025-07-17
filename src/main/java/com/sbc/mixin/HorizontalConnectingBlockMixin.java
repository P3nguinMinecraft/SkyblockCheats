package com.sbc.mixin;

import com.sbc.util.Config;
import com.sbc.util.ScoreboardUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorizontalConnectingBlock.class)
public class HorizontalConnectingBlockMixin {
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void modifyHitboxOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state == null) return;
        if (Boolean.TRUE.equals(Config.getConfig("timite-hitboxes")) && (ScoreboardUtils.contains("The Mountaintop") || ScoreboardUtils.contains("ф"))){
            if (state.isOf(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE) || state.isOf(Blocks.BLUE_STAINED_GLASS_PANE) || state.isOf(Blocks.PURPLE_STAINED_GLASS_PANE)){
                cir.setReturnValue(VoxelShapes.fullCube());
            }
        }
    }
}
