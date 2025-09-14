package com.sbc.feature.hunting;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;


public class RepeatFusion {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ItemStack BOOK_OFF_STACK = new ItemStack(Items.BOOK);
    private static final ItemStack BOOK_ON_STACK = new ItemStack(Items.KNOWLEDGE_BOOK);
    private static final int REPEAT_SLOT = 47;
    private static final int CONFIRM_SLOT = 33;

    public static Screen currentScreen;
    private static ScreenHandler handler;
    public static volatile boolean active = false;
    public static volatile boolean inGui = false;
    private static int closeCounter = 0;

    public static void init() {
        BOOK_OFF_STACK.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§2[SBC] §r§cRepeat Fusion"));
        BOOK_ON_STACK.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§2[SBC] §r§aRepeat Fusion"));

        ScreenEvents.AFTER_INIT.register((client,screen,scaledWidth,scaledHeight)-> {
            if (!(boolean) Config.getConfig("fusion-helper")) return;
            if (!(screen instanceof HandledScreen<?>)) return;
            if (!screen.getTitle().getString().contains("Fusion Box") && !screen.getTitle().getString().contains("Confirm Fusion")) return;
            stop();
            start(screen);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (active){
                if (client.currentScreen != null && (client.currentScreen.getTitle().getString().contains("Fusion Box") || client.currentScreen.getTitle().getString().contains("Confirm Fusion"))){
                    closeCounter = 0;
                }
                else {
                    closeCounter = Math.max(closeCounter + 1, 100);
                    if (closeCounter == 100) {
                        active = false;
                    }
                }
            }
            if (!inGui || client.currentScreen != currentScreen) {
                stop();
                return;
            }
            System.out.println(currentScreen.getTitle().getString());
            if (inGui && !(handler.getSlot(0).getStack().isOf(Items.KNOWLEDGE_BOOK) || handler.getSlot(0).getStack().isOf(Items.BOOK))){
                injectBook(active);
            }
            if (active) {
                tick();
            }
        });
    }

    public static void start(Screen screen){
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
        handler = handledScreen.getScreenHandler();
        currentScreen = screen;
        inGui = true;
        closeCounter = 0;
    }

    public static void stop(){
        if (inGui) {
            inGui = false;
            currentScreen = null;
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
        if (currentScreen.getTitle().getString().contains("Fusion Box")){
            Slot slot = handler.getSlot(REPEAT_SLOT);
            ItemStack stack = slot.getStack();
            if (stack.isOf(Items.BLACK_STAINED_GLASS_PANE)){
                active = false;
                injectBook(active);
                ChatUtils.sendMessage("§2[SBC] §r§cNo previous fusion detected!");
            }
            else if (stack.isOf(Items.PLAYER_HEAD) && (stack.getCustomName().getString().contains("Repeat Previous Fusion"))){
                client.execute(() -> {
                    client.interactionManager.clickSlot(handler.syncId, REPEAT_SLOT, 0, SlotActionType.CLONE, client.player);
                });
            }
        }
        else if (currentScreen.getTitle().getString().contains("Confirm Fusion")){
            Slot slot = handler.getSlot(CONFIRM_SLOT);
            ItemStack stack = slot.getStack();
            if (stack.isOf(Items.LIME_TERRACOTTA) && (stack.getCustomName().getString().contains("Fusion"))){
                client.execute(() -> {
                    client.interactionManager.clickSlot(handler.syncId, CONFIRM_SLOT, 0, SlotActionType.CLONE, client.player);
                });
            }
        }
        else {
            // how did we get here?
            ChatUtils.sendMessage("Repeat Fusion Error: How did we get here?");
            stop();
            injectBook(active);
        }
    }

}
