package com.sbc.feature.skyblock;

import com.sbc.data.Enchants;
import com.sbc.util.Config;
import com.sbc.util.ItemUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AnvilHelper {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ItemStack DIAMOND_BLOCK_STACK = new ItemStack(Items.DIAMOND_BLOCK);
    private static final ItemStack BOOK_OFF_STACK = new ItemStack(Items.BOOK);
    private static final ItemStack BOOK_ON_STACK = new ItemStack(Items.KNOWLEDGE_BOOK);
    private static final int INPUT1_SLOT = 29;
    private static final int INPUT2_SLOT = 33;
    private static final int OUTPUT_SLOT = 13;
    private static final int ACTION_SLOT = 22;

    public static Screen currentScreen;
    private static ScreenHandler handler;
    public static volatile boolean active = false;
    public static volatile boolean injected = false;
    private static HelperState status = HelperState.IDLE;
    private static int[] slots = new int[2]; // -1: Not found
    private static long clientTicks = 0;

    public static void init() {
        BOOK_OFF_STACK.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§2[SBC] §r§cAnvil Helper"));
        BOOK_ON_STACK.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§2[SBC] §r§aAnvil Helper"));

        ScreenEvents.AFTER_INIT.register((client,screen,scaledWidth,scaledHeight)-> {
            if (!(boolean) Config.getConfig("anvil-helper")) return;
            if (!(screen instanceof HandledScreen<?>)) return;
            if (!screen.getTitle().getString().contains("Anvil")) return;

            new Thread(() -> AnvilHelper.start(screen)).start();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTicks++;
            if (client.currentScreen != currentScreen) {
                stop();
                return;
            }
            if (injected && !(handler.getSlot(0).getStack().isOf(Items.KNOWLEDGE_BOOK) || handler.getSlot(0).getStack().isOf(Items.BOOK))){
                injectBook(active);
            }
            if (!active){
                status = HelperState.IDLE;
                return;
            }
            if (clientTicks % (int) Config.getConfig("anvil-delay") == 0) tick();
        });
    }

    public static void start(Screen screen){
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
        handler = handledScreen.getScreenHandler();
        currentScreen = screen;
        injected = true;
    }

    public static void stop(){
        if (injected) {
            injected = false;
            currentScreen = null;
            active = false;
        }
    }

    private static void injectBook(Boolean active){
        handler.getSlot(0).setStack(active ? BOOK_ON_STACK.copy() : BOOK_OFF_STACK.copy());
    }

    public static void registerClick(){
        active = !active;
        injectBook(active);
    }

    private static void tick(){
        System.out.print("tick - ");
        status = getStatus();
        doAction(status);
    }

    private static HelperState getStatus(){
        if (handler.getSlot(ACTION_SLOT).getStack().isOf(Items.DIAMOND_BLOCK)){
            return HelperState.WAIT;
        }
        if (handler.getSlot(ACTION_SLOT).getStack().isOf(Items.OAK_SIGN)){
            return HelperState.RETRIEVE;
        }
        if (!handler.getSlot(OUTPUT_SLOT).getStack().isOf(Items.BARRIER)){
            return HelperState.COMBINE;
        }
        if (slots[1] == -2){
            return HelperState.WAIT;
        }
        if (!handler.getSlot(INPUT1_SLOT).getStack().isOf(Items.AIR) && handler.getSlot(INPUT2_SLOT).getStack().isOf(Items.AIR)){
            return HelperState.INPUT2;
        }
        if (slots[0] == -2){
            return HelperState.WAIT;
        }
        slots = getSlots();
        if (slots[0] != -1 && handler.getSlot(INPUT1_SLOT).getStack().isOf(Items.AIR) && handler.getSlot(INPUT2_SLOT).getStack().isOf(Items.AIR)){
            return HelperState.INPUT1;
        }

        return HelperState.IDLE;
    }

    private static int[] getSlots(){
        int[] slots = new int[2];
        Arrays.fill(slots, -1);
        HashMap<HashMap<String, Integer>, Integer> invEnchants = new HashMap<>();
        List<ItemStack> items = handler.getStacks();
        perItem:
        for (int i = 0; i < items.size(); i++){
            ItemStack item = items.get(i);
            if (!item.isOf(Items.ENCHANTED_BOOK)) continue;
            HashMap<String, Integer> enchants = ItemUtils.getEnchants(item);
            if (enchants.size() != 1) continue;

            for (String name: enchants.keySet()){
                int level = enchants.get(name);
                if (!Enchants.underMax(name, level)){
                    continue perItem;
                }
            }

            for (HashMap<String, Integer> candidate : invEnchants.keySet()){
                if (ItemUtils.matchesEnchants(item, candidate)){
                    slots[0] = invEnchants.get(candidate);
                    slots[1] = i;
                    return slots;
                }
            }
            invEnchants.put(enchants, i);
        }
        return slots;
    }

    private static void doAction(HelperState state){
        System.out.println(state.name());
        if (state == HelperState.RETRIEVE || state == HelperState.COMBINE){
            client.execute(() -> {
                client.interactionManager.clickSlot(handler.syncId, ACTION_SLOT, 1, SlotActionType.PICKUP, client.player);
                client.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
            });
            handler.getSlot(ACTION_SLOT).setStack(DIAMOND_BLOCK_STACK.copy());
            Arrays.fill(slots, -1);
            return;
        }
        if (state == HelperState.INPUT2){
            client.execute(() -> {
                client.interactionManager.clickSlot(handler.syncId, slots[1], 1, SlotActionType.QUICK_MOVE, client.player);
            });
            slots[1] = -2;
            return;
        }
        if (state == HelperState.INPUT1){
            client.execute(() -> {
                client.interactionManager.clickSlot(handler.syncId, slots[0], 1, SlotActionType.QUICK_MOVE, client.player);
            });
            slots[0] = -2;
            return;
        }
    }

    enum HelperState {
        IDLE,
        WAIT,
        INPUT1,
        INPUT2,
        COMBINE,
        RETRIEVE
    }
}
