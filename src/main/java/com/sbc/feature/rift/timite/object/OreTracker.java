package com.sbc.feature.rift.timite.object;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.InteractUtils;
import net.minecraft.util.math.BlockPos;

public  class OreTracker {

    public final BlockPos pos;
    public final OreType type;
    public int progress;
    public int cooldown = 0;
    public long lastAged;

    public OreTracker(BlockPos pos, OreType type, Long clientTick){
        this.pos = pos;
        this.type = type;
        this.progress = 0;
        this.lastAged = clientTick;
    }

    public void tick(){
        cooldown = Math.max(0, cooldown-1);
    }

    public void attemptAge(long ticks){
        if (cooldown == 0){
            cooldown = (int) Config.getConfig("gun-cd");
            InteractUtils.rightClick();
            lastAged = ticks;
        }
    }


}