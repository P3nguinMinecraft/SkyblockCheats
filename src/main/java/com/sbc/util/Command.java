package com.sbc.util;

import com.sbc.command.*;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Command {
    public static void registerAll() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
        	SBC.register(dispatcher, registryAccess);
            ConfigSet.register(dispatcher, registryAccess);
        	ConfigList.register(dispatcher, registryAccess);
        	ConfigReset.register(dispatcher, registryAccess);
            GrottoSearchToggle.register(dispatcher, registryAccess);
            GrottoSearchClear.register(dispatcher, registryAccess);
            GrottoSearchList.register(dispatcher, registryAccess);
            GrottoSearchScan.register(dispatcher, registryAccess);
            Uptime.register(dispatcher, registryAccess);
            PlaySound.register(dispatcher, registryAccess);
            LookBlock.register(dispatcher, registryAccess);
            LookPos.register(dispatcher, registryAccess);
            SendCoords.register(dispatcher, registryAccess);
            ServerTPS.register(dispatcher, registryAccess);
            Action.register(dispatcher, registryAccess);
            ServerHistoryList.register(dispatcher, registryAccess);
            ServerHistoryClear.register(dispatcher, registryAccess);
            Debug.register(dispatcher, registryAccess);
            Render.register(dispatcher, registryAccess);
        });
    }
}