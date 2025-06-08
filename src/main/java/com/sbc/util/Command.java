package com.sbc.util;

import com.sbc.command.ConfigList;
import com.sbc.command.ConfigRemove;
import com.sbc.command.ConfigSet;
import com.sbc.command.LookBlock;
import com.sbc.command.LookPos;
import com.sbc.command.PlaySound;
import com.sbc.command.SearchClear;
import com.sbc.command.SearchList;
import com.sbc.command.SearchScan;
import com.sbc.command.SearchToggle;
import com.sbc.command.SendCoords;
import com.sbc.command.ServerTPS;
import com.sbc.command.UptimeCommand;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Command {
    public static void registerAll() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
        	ConfigSet.register(dispatcher, registryAccess);
        	ConfigList.register(dispatcher, registryAccess);
        	ConfigRemove.register(dispatcher, registryAccess);
            SearchToggle.register(dispatcher, registryAccess);
            SearchClear.register(dispatcher, registryAccess);
            SearchList.register(dispatcher, registryAccess);
            SearchScan.register(dispatcher, registryAccess);
            UptimeCommand.register(dispatcher, registryAccess);
            PlaySound.register(dispatcher, registryAccess);
            LookBlock.register(dispatcher, registryAccess);
            LookPos.register(dispatcher, registryAccess);
            SendCoords.register(dispatcher, registryAccess);
            ServerTPS.register(dispatcher, registryAccess);
        });
    }
}