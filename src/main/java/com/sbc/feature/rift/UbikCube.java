package com.sbc.feature.rift;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.ItemUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class UbikCube {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ItemStack ORANGE_GLASS_PANE_STACK = new ItemStack(Items.ORANGE_STAINED_GLASS_PANE);
    private static Screen currentScreen;
    private static ScreenHandler handler;
    public static volatile boolean active = false;


    public static void init() {
        ScreenEvents.AFTER_INIT.register((client,screen,scaledWidth,scaledHeight)-> {
            if (!(boolean) Config.getConfig("auto-ubik")) return;
            if (!(screen instanceof HandledScreen<?>)) return;
            if (!screen.getTitle().getString().contains("Split or Steal")) return;
            if (screen.getTitle().getString().contains("Stats")) return;

            new Thread(() -> UbikCube.start(screen)).start();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!active || client.currentScreen != currentScreen) {
                stop();
                return;
            }
            for (int i = 0; i < handler.slots.size(); i++) {
                Slot slot = handler.slots.get(i);
                boolean hasClicked = false;
                if (slot.getStack().getItem() == Items.RED_STAINED_GLASS_PANE) {
                    if (!hasClicked) {
                        client.execute(() -> {
                            client.interactionManager.clickSlot(handler.syncId, slot.id, 1, SlotActionType.PICKUP, client.player);
                        });
                        hasClicked = true;
                    }
                    slot.setStack(ORANGE_GLASS_PANE_STACK.copy());
                }
            }
        });
    }

    public static void start(Screen screen){
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
        handler = handledScreen.getScreenHandler();
        boolean foundStats = false;
        do {
            for (ItemStack stack : handler.getStacks()) {
                if (stack.getItem().equals(Items.PLAYER_HEAD)) {
                    String lore = ItemUtils.getLoreAsString(stack);
                    if (lore.contains("Playstyle:")) {
                        foundStats = true;
                        if (ItemUtils.getLoreAsString(stack).contains("100% SPLIT")) {
                            active = true;
                            currentScreen = screen;
                            handler = handledScreen.getScreenHandler();
                            ChatUtils.addMessage("§2[SBC] §r§dAutoUbik started");
                            break;
                        }
                    }
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // nothing
            }
        } while (client.currentScreen == screen && !foundStats);
    }

    public static void stop(){
        if (active) {
            active = false;
            currentScreen = null;
        }
    }
}
