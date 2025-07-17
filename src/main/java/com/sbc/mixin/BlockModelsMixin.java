package com.sbc.mixin;

import com.sbc.util.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockModels.class)
public class BlockModelsMixin {
    private static boolean lumieConfig = Boolean.TRUE.equals(Config.getConfig("lumie-hideblocks"));
    private static boolean leavesConfig = Boolean.TRUE.equals(Config.getConfig("hide-leaves"));
    private static final BlockStateModel EMPTY = new BlockStateModel() {
        @Override
        public void addParts(Random random, List<BlockModelPart> parts) {
        }

        @Override
        public Sprite particleSprite() {
            return MinecraftClient.getInstance()
                .getBakedModelManager()
                .getMissingModel()
                .particleSprite();
        }
    };

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void wipeModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        boolean lumieConfigCurrent = Boolean.TRUE.equals(Config.getConfig("lumie-hideblocks"));
        boolean leavesConfigCurrent = Boolean.TRUE.equals(Config.getConfig("hide-leaves"));
        if (lumieConfigCurrent != lumieConfig){
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().worldRenderer.reload();
            });
            lumieConfig = lumieConfigCurrent;
        }
        else if (leavesConfigCurrent != leavesConfig){
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().worldRenderer.reload();
            });
            leavesConfig = leavesConfigCurrent;
        }
        if (shouldHide(state)) {
            cir.setReturnValue(EMPTY);
        }
    }

    @Unique
    private static boolean shouldHide(BlockState state) {
        if (state == null) return false;
        if (Boolean.TRUE.equals(Config.getConfig("lumie-hideblocks"))){
            if (state.isOf(Blocks.KELP) || state.isOf(Blocks.KELP_PLANT) || state.isOf(Blocks.SEAGRASS) || state.isOf(Blocks.TALL_SEAGRASS)){
                return true;
            }
        }
        if (Boolean.TRUE.equals(Config.getConfig("hide-leaves"))){
            return state.isOf(Blocks.AZALEA_LEAVES) || state.isOf(Blocks.MANGROVE_LEAVES);
        }
        return false;
    }
}
