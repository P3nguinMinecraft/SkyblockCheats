package com.sbc.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class Skyblock {
    public static volatile boolean inCH = false;
    public static volatile boolean inMountaintop = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(Skyblock::tick);
    }
    public static void tick(MinecraftClient client){
        inCH = ScoreboardUtils.contains("Jungle")
                || ScoreboardUtils.contains("Mithril Deposits")
                || ScoreboardUtils.contains("Precursor Remnants")
                || ScoreboardUtils.contains("Goblin Holdout")
                || ScoreboardUtils.contains("Crystal Nucleus");

        inMountaintop = ScoreboardUtils.contains("The Mountaintop");
    }
}
