package com.sbc.util;

import org.lwjgl.glfw.GLFW;

import com.sbc.feature.GhostBlock;
import com.sbc.feature.GrottoSearchManager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class Keybind {
    private static KeyBinding searchToggleKey;
    private static KeyBinding searchScanKey;
    private static KeyBinding searchClearKey;
    private static KeyBinding ghostBlockKey;

    public static void init() {
        searchToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_toggle", GLFW.GLFW_KEY_G, "category.sbc"));

        searchScanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_scan", GLFW.GLFW_KEY_B, "category.sbc"));

        searchClearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_clear", GLFW.GLFW_KEY_H, "category.sbc"));
        
        ghostBlockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.sbc.ghost_block", GLFW.GLFW_KEY_V, "category.sbc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (searchToggleKey.wasPressed()) {
                GrottoSearchManager.toggleSearch();
            }
            while (searchScanKey.wasPressed()) {
            	GrottoSearchManager.scan();
            }
            while (searchClearKey.wasPressed()) {
                GrottoSearchManager.clearSearch();
            }
			GhostBlock.handleKeyPress(ghostBlockKey.isPressed());
        });
    }
}
