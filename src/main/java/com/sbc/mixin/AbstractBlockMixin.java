package com.sbc.mixin;

import com.sbc.util.Config;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    private static final VoxelShape ONE_PICKLE_SHAPE = Block.createColumnShape(4.0, 0.0, 6.0);
    private static final VoxelShape TWO_PICKLES_SHAPE = Block.createColumnShape(10.0, 0.0, 6.0);
    private static final VoxelShape THREE_PICKLES_SHAPE = Block.createColumnShape(12.0, 0.0, 6.0);
    private static final VoxelShape FOUR_PICKLES_SHAPE = Block.createColumnShape(12.0, 0.0, 7.0);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void modifyHitboxOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state == null) return;
        if (Boolean.TRUE.equals(Config.getConfig("hide-leaves"))) {
            if (state.isOf(Blocks.AZALEA_LEAVES) || state.isOf(Blocks.MANGROVE_LEAVES)) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void modifyCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (Boolean.TRUE.equals(Config.getConfig("lumie-hitboxes"))) {
            if (state.isOf(Blocks.SEA_PICKLE)) {
                cir.setReturnValue(switch (state.get(Properties.PICKLES)) {
                    case 2 -> TWO_PICKLES_SHAPE;
                    case 3 -> THREE_PICKLES_SHAPE;
                    case 4 -> FOUR_PICKLES_SHAPE;
                    default -> ONE_PICKLE_SHAPE;
                });
            }
        }

        if (Boolean.TRUE.equals(Config.getConfig("hide-leaves"))) {
            if (state.isOf(Blocks.AZALEA_LEAVES) || state.isOf(Blocks.MANGROVE_LEAVES)) {
                cir.setReturnValue(VoxelShapes.fullCube());
            }
        }
    }
}
