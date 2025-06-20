package com.sbc.util;

import com.mojang.authlib.properties.Property;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;


public class ItemUtils {
	public static String getHeadTexture(ItemStack stack) {
        if (!stack.isOf(Items.PLAYER_HEAD) || !stack.contains(DataComponentTypes.PROFILE)) return "";

        ProfileComponent profile = stack.get(DataComponentTypes.PROFILE);
        if (profile == null) return "";

        return profile.properties().get("textures").stream()
                .map(Property::value)
                .findFirst()
                .orElse("");
    }
}
