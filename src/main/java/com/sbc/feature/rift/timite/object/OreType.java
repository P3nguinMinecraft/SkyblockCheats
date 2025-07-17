package com.sbc.feature.rift.timite.object;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum OreType {
    YOUNGITE(1, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE),
    TIMITE(2, Blocks.BLUE_STAINED_GLASS_PANE),
    OBSOLITE(3, Blocks.PURPLE_STAINED_GLASS_PANE),
    OTHER(0, Blocks.AIR);

    public final int stage;
    public final Block block;
    OreType(int stage, Block block){
        this.stage = stage;
        this.block = block;
    }

    public static OreType getType(Block block){
        if (block.equals(YOUNGITE.block)) return YOUNGITE;
        if (block.equals(TIMITE.block)) return TIMITE;
        if (block.equals(OBSOLITE.block)) return OBSOLITE;

        return OTHER;
    }
}