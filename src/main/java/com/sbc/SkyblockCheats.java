package com.sbc;

import com.sbc.command.Render;
import com.sbc.feature.mining.PowderChest;
import com.sbc.feature.rift.AutoImpel;
import com.sbc.feature.rift.UbikCube;
import com.sbc.feature.rift.timite.TimiteHelper;
import com.sbc.feature.rift.timite.TimiteState;
import com.sbc.feature.server.ServerHistory;
import com.sbc.feature.skyblock.AnvilHelper;
import com.sbc.feature.skyblock.AutoMelody;
import com.sbc.feature.skyblock.AutoVisit;
import com.sbc.feature.skyblock.beachball.BeachBall;
import com.sbc.feature.tool.Autoclicker;
import com.sbc.feature.tool.GhostBlock;
import com.sbc.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbc.render.RenderHelper;
import com.sbc.render.RenderQueue;

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
        Render.init();
        ScoreboardUtils.init();
        TimiteHelper.init();
        TimiteState.init();
        UbikCube.init();
        AutoVisit.init();
        AnvilHelper.init();
        PowderChest.init();
        config.Config.INSTANCE.init();
        LOGGER.info("SkyblockCheats started!");
        ChatUtils.sendDebugMessage("[SBC] DEBUG MODE is enabled!");
    }
}
