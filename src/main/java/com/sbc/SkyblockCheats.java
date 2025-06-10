package com.sbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbc.feature.AutoMelody;
import com.sbc.feature.GhostBlock;
import com.sbc.feature.ServerHistory;
import com.sbc.feature.TPSTracker;
import com.sbc.render.RenderHelper;
import com.sbc.render.RenderQueue;
import com.sbc.util.ChatUtils;
import com.sbc.util.Command;
import com.sbc.util.Config;
import com.sbc.util.Keybind;
import com.sbc.util.World;

import net.fabricmc.api.ClientModInitializer;

public class SkyblockCheats implements ClientModInitializer {
    public static final String NAMESPACE = "skyblockcheats";
    public static final String MOD_ID = "skyblockcheats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting SkyblockCheats...");
        Config.init();
        Keybind.init();
        RenderHelper.init();
        RenderQueue.init();
        ChatUtils.init();
        Command.registerAll();
        World.init();
        AutoMelody.init();
        GhostBlock.init();
        TPSTracker.init();
        ServerHistory.init();
        LOGGER.info("SkyblockCheats started!");
    }
}
