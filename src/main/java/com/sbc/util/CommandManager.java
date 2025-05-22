package com.sbc.util;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.sbc.command.*;

public class CommandManager {
    public static void registerAll() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
        	ConfigSet.register(dispatcher, registryAccess);
        	ConfigList.register(dispatcher, registryAccess);
        	ConfigRemove.register(dispatcher, registryAccess);
            SearchToggle.register(dispatcher, registryAccess);
            SearchClear.register(dispatcher, registryAccess);
            SearchList.register(dispatcher, registryAccess);
            SearchScan.register(dispatcher, registryAccess);
        });
    }
}