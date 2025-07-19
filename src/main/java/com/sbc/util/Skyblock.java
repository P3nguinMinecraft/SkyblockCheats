package com.sbc.util;

import com.sbc.data.Constants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class Skyblock {
    public static volatile boolean inCH = false;
    public static volatile boolean inMountaintop = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(Skyblock::tick);
    }
    public static void tick(MinecraftClient client){
        inCH = Constants.CRYSTAL_HOLLOWS_LOCATIONS.stream().anyMatch(ScoreboardUtils::contains);

        inMountaintop = ScoreboardUtils.contains("The Mountaintop");
    }
}
