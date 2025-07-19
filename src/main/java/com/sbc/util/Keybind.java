package com.sbc.util;

import org.lwjgl.glfw.GLFW;

import com.sbc.feature.tool.Autoclicker;
import com.sbc.feature.tool.GhostBlock;
import com.sbc.feature.mining.GrottoSearchManager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class Keybind {
    private static KeyBinding searchToggleKey;
    private static KeyBinding searchScanKey;
    private static KeyBinding ghostBlockKey;
    private static KeyBinding autoLeftClickKey;
    private static KeyBinding autoRightClickKey;

    public static void init() {
        searchToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_toggle", GLFW.GLFW_KEY_X, "category.sbc"));

        searchScanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.search_scan", GLFW.GLFW_KEY_B, "category.sbc"));
        
        ghostBlockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.sbc.ghost_block", GLFW.GLFW_KEY_V, "category.sbc"));
        
        autoLeftClickKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.sbc.auto_left_click", GLFW.GLFW_KEY_G, "category.sbc"));

        autoRightClickKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.sbc.auto_right_click", GLFW.GLFW_KEY_H, "category.sbc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (searchToggleKey.wasPressed()) {
                GrottoSearchManager.toggleSearch();
            }
            while (searchScanKey.wasPressed()) {
            	GrottoSearchManager.scan();
            }
            while (autoLeftClickKey.wasPressed()) {
            	Autoclicker.toggleAutoLeftClick();
			}
            while (autoRightClickKey.wasPressed()) {
				Autoclicker.toggleAutoRightClick();
			}
            
			GhostBlock.handleKeyPress(ghostBlockKey.isPressed());
        });
    }
}
