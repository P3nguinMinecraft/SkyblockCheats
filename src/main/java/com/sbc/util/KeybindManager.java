package com.sbc.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import com.sbc.feature.SearchManager;

public class KeybindManager {
    private static KeyBinding toggleSearchKey;
    private static KeyBinding clearSearchKey;

    public static void init() {
        toggleSearchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.toggle_search", GLFW.GLFW_KEY_G, "category.sbc"));

        clearSearchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbc.clear_search", GLFW.GLFW_KEY_H, "category.sbc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleSearchKey.wasPressed()) {
                SearchManager.toggleSearch();
            }
            while (clearSearchKey.wasPressed()) {
                SearchManager.clearSearch();
            }
        });
    }
}
