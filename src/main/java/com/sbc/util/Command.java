package com.sbc.util;

import com.sbc.command.Action;
import com.sbc.command.ConfigList;
import com.sbc.command.ConfigReset;
import com.sbc.command.ConfigSet;
import com.sbc.command.Debug;
import com.sbc.command.GrottoSearchClear;
import com.sbc.command.GrottoSearchList;
import com.sbc.command.GrottoSearchScan;
import com.sbc.command.GrottoSearchToggle;
import com.sbc.command.LookBlock;
import com.sbc.command.LookPos;
import com.sbc.command.PlaySound;
import com.sbc.command.SendCoords;
import com.sbc.command.ServerHistoryClear;
import com.sbc.command.ServerHistoryList;
import com.sbc.command.ServerTPS;
import com.sbc.command.Uptime;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Command {
    public static void registerAll() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
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
        });
    }
}