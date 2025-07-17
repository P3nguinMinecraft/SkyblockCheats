package com.sbc.feature.skyblock;

import com.sbc.util.Config;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class AutoVisit {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static void init() {
        ScreenEvents.AFTER_INIT.register((client,screen,scaledWidth,scaledHeight)-> {
            if (!(boolean) Config.getConfig("auto-visit")) return;
            if (!(screen instanceof HandledScreen<?>)) return;
            if (!screen.getTitle().getString().contains("Visit")) return;

            new Thread(() -> AutoVisit.start(screen)).start();
        });
    }

    public static void start(Screen screen){
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
        ScreenHandler handler = handledScreen.getScreenHandler();
        Boolean clicked = false;
        do {
            for (int i = 0; i < handler.slots.size(); i++) {
                Slot slot = handler.slots.get(i);
                ItemStack stack = slot.getStack();
                if (stack.getItem().equals(Items.PLAYER_HEAD) && stack.getCustomName() != null && stack.getCustomName().getString().contains("Visit player island")) {
                    client.execute(() -> {
                        client.interactionManager.clickSlot(handler.syncId, slot.id, 1, SlotActionType.PICKUP, client.player);
                    });
                    clicked = true;
                    break;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (client.currentScreen == screen && !clicked);
    }
}
