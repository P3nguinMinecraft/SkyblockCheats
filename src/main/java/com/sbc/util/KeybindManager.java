package com.sbc.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import com.sbc.feature.SearchManager;

public class KeybindManager {
    private static KeyBinding searchToggleKey;
    private static KeyBinding searchScanKey;
    private static KeyBinding searchClearKey;

    public static void init() {
        searchToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_toggle", GLFW.GLFW_KEY_G, "category.sbc"));
        
        searchScanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_scan", GLFW.GLFW_KEY_B, "category.sbc"));

        searchClearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_clear", GLFW.GLFW_KEY_H, "category.sbc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (searchToggleKey.wasPressed()) {
                SearchManager.toggleSearch();
            }
            while (searchScanKey.wasPressed()) {
            	SearchManager.scan();
            }
            while (searchClearKey.wasPressed()) {
                SearchManager.clearSearch();
            }
        });
    }
}
