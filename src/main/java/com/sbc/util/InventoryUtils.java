package com.sbc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class InventoryUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static ArrayList<ItemStack> getItems(){
        ArrayList<ItemStack> items = new ArrayList<>();
        if (client.player == null) return items;
        PlayerInventory inv = client.player.getInventory();
        inv.forEach(items::add);
        return items;
    }

    public static ArrayList<ItemStack> getItems(Predicate<ItemStack> cond){
        ArrayList<ItemStack> filtered = new ArrayList<>();
        ArrayList<ItemStack> items = getItems();
        items.forEach(itemStack -> {
            if (cond.test(itemStack)){
                filtered.add(itemStack);
            }
        });

        return filtered;
    }

    public static ArrayList<ItemStack> getItems(Item item){
        return getItems(itemStack -> {
            return itemStack.getItem().equals(item);
        });
    }

    public static ArrayList<ItemStack> getItems(String id){
        return getItems(itemStack -> {
            return ItemUtils.matchesId(itemStack, id);
        });
    }

    public static ArrayList<ItemStack> getItemsEnchants(HashMap<String, Integer> enchants){
        return getItems(itemStack -> {
            return ItemUtils.matchesEnchants(itemStack, enchants);
        });
    }

    public static ArrayList<ItemStack> getItemsLore(String lore){
        return getItems(itemStack -> {
            return ItemUtils.matchesLore(itemStack, lore);
        });
    }

    public static ArrayList<Integer> getItemSlots(Predicate<ItemStack> cond){
        ArrayList<Integer> slots = new ArrayList<>();
        ArrayList<ItemStack> items = getItems();
        items.forEach(itemStack -> {
            if (cond.test(itemStack)){
                slots.add(items.indexOf(itemStack));
            }
        });

        return slots;
    }

    public static ArrayList<Integer> getItemSlots(Item item){
        return getItemSlots(itemStack -> {
            return itemStack.getItem().equals(item);
        });
    }

    public static ArrayList<Integer> getItemSlots(String id){
        return getItemSlots(itemStack -> {
            return ItemUtils.matchesId(itemStack, id);
        });
    }

    public static ArrayList<Integer> getItemSlotsEnchants(HashMap<String, Integer> enchants){
        return getItemSlots(itemStack -> {
            return ItemUtils.matchesEnchants(itemStack, enchants);
        });
    }

    public static ArrayList<Integer> getItemSlotsLore(String lore){
        return getItemSlots(itemStack -> {
            return ItemUtils.matchesLore(itemStack, lore);
        });
    }

    public static int countItems(ArrayList<Integer> slots){
        int count = 0;
        ArrayList<ItemStack> items = getItems();
        for (int slot : slots){
            count += items.get(slot).getCount();
        }
        return count;
    }

    public static int countItems(Predicate<ItemStack> cond){
        return countItems(getItemSlots(cond));
    }

    public static int countItems(Item item){
        return countItems(getItemSlots(item));
    }

    public static int countItems(String id){
        return countItems(getItemSlots(id));
    }

    public static void setSlot(int slot) {
        if (client.player == null) return;
        if (slot >= 0 && slot <= 8) {
            client.player.getInventory().setSelectedSlot(slot);
        }
        else {
            throw new InvalidParameterException("Slot is not in range!");
        }
    }
}
