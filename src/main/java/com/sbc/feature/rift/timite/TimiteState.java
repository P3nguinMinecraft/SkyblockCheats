package com.sbc.feature.rift.timite;

import com.sbc.util.Config;
import com.sbc.util.ScoreboardUtils;
import com.sbc.util.Skyblock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TimiteState {
    private static boolean before = getTimite();
    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(TimiteState::tick);
    }

    private static void tick(MinecraftClient minecraftClient) {
        if (before != getTimite()){
            before = getTimite();
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().worldRenderer.reload();
            });
        }
    }

    public static boolean getTimite(){
        return Boolean.TRUE.equals(Config.getConfig("timite-hitboxes")) && Skyblock.inMountaintop;
    }
}
