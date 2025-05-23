package com.sbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbc.render.Render;
import com.sbc.util.ChatUtils;
import com.sbc.util.CommandManager;
import com.sbc.util.ConfigManager;
import com.sbc.util.KeybindManager;

import net.fabricmc.api.ClientModInitializer;

public class SkyblockCheats implements ClientModInitializer {
    public static final String MOD_ID = "autoblockfinder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing AutoBlockFinder...");
        ConfigManager.init();
        KeybindManager.init();
        Render.init();
        ChatUtils.init();
        CommandManager.registerAll();
        LOGGER.info("AutoBlockFinder initialized!");

    }
}
