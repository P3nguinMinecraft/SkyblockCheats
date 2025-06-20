package com.sbc;

import com.sbc.feature.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbc.render.RenderHelper;
import com.sbc.render.RenderQueue;
import com.sbc.util.ChatUtils;
import com.sbc.util.Command;
import com.sbc.util.Config;
import com.sbc.util.DebugList;
import com.sbc.util.DelayUtils;
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
        ServerHistory.init();
        Autoclicker.init();
        AutoImpel.init();
        DebugList.init();
        DelayUtils.init();
        BeachBall.init();
        LOGGER.info("SkyblockCheats started!");
        ChatUtils.sendDebugMessage("DEBUG MODE is enabled!");
    }
}
