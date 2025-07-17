package com.sbc.util;

import com.mojang.authlib.properties.Property;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @SuppressWarnings("deprecation")
    public static @NotNull NbtCompound getCustomData(@NotNull ComponentHolder stack) {
        return stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
    }

    public static @Nullable String getId(ComponentHolder stack){
        NbtCompound customData = getCustomData(stack);
        return customData.getString("id").orElse(null);
    }

    public static boolean matchesId(ComponentHolder stack, String matchId){
        String id = getId(stack);
        return id != null && id.equalsIgnoreCase(matchId);
    }

    public static @NotNull HashMap<String, Integer> getEnchants(ComponentHolder stack){
        NbtCompound customData = getCustomData(stack);
        NbtCompound enchantsCompound = customData.getCompound("enchantments").orElse(null);
        HashMap<String, Integer> enchants = new HashMap<String, Integer>();
        if (enchantsCompound != null){
            for (String key : enchantsCompound.getKeys()) {
                enchants.put(key, enchantsCompound.getInt(key).orElse(0));
            }
        }
        return enchants;
    }

    public static boolean matchesEnchants(ComponentHolder stack, HashMap<String, Integer> matchEnchants){
        HashMap<String, Integer> enchants = getEnchants(stack);
        if (enchants.size() != matchEnchants.size()) return false;
        for (Map.Entry<String, Integer> entry : matchEnchants.entrySet()) {
            if (!enchants.containsKey(entry.getKey())) return false;
            if (!enchants.get(entry.getKey()).equals(entry.getValue())) return false;
        }
        return true;
    }


    public static @NotNull List<Text> getLore(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).styledLines();
    }

    public static @NotNull String getLoreAsString(ItemStack stack){
        List<Text> lore = getLore(stack);
        String loreString = "";
        for (Text text : lore){
            loreString = loreString + Formatting.strip(text.getString());
        }
        return loreString;
    }

    public static boolean matchesLore(ItemStack stack, String matchLore){
        String lore = getLoreAsString(stack);
        return matchLore.equals(lore);
    }
}
