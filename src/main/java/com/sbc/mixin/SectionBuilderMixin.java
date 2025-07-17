package com.sbc.mixin;

import com.sbc.util.Config;
import com.sbc.util.ScoreboardUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionBuilder.class)
public abstract class SectionBuilderMixin implements ResourceReloader {
    private static volatile boolean timiteConfig = Boolean.TRUE.equals(Config.getConfig("timite-hitboxes")) && ScoreboardUtils.contains("The Mountaintop");

    @Redirect(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkRendererRegion;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState redirectGetBlockState(ChunkRendererRegion instance, BlockPos pos) {
        BlockState original = instance.getBlockState(pos);
        boolean timiteConfigCurrent = Boolean.TRUE.equals(Config.getConfig("timite-hitboxes")) && ScoreboardUtils.contains("The Mountaintop");
        if (timiteConfigCurrent) {
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
        if (timiteConfigCurrent != timiteConfig){
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().worldRenderer.reload();
            });
            timiteConfig = timiteConfigCurrent;
        }

        return original;
    }
}
