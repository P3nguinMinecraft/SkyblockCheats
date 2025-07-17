package com.sbc.feature.skyblock;

import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class AutoMelody {
	private static final ItemStack DIAMOND_BLOCK_STACK = new ItemStack(Items.DIAMOND_BLOCK);
	private static final ItemStack QUARTZ_BLOCK_STACK = new ItemStack(Items.QUARTZ_BLOCK);

    private static Screen currentScreen;
    private static ScreenHandler handler;
    private static int tick = 0;
    public static volatile boolean active = false;
    private static final int[] delayedClick = new int[2];

    public static void init() {
    	delayedClick[0] = -1;
    	delayedClick[1] = -1;

		DIAMOND_BLOCK_STACK.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§2[SBC] §r§dAutoMelody"));

    	ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
    		if (!(boolean) Config.getConfig("auto-melody")) return;
    		if (!(screen instanceof HandledScreen<?>)) return;
    	    if (!screen.getTitle().getString().contains("Harp")) return;

    	    AutoMelody.start(screen);
    	});
    	
    	ClientTickEvents.END_CLIENT_TICK.register(client -> {
    		tick++;
    	    if (!active || client.currentScreen != currentScreen) {
    	        stop();
    	        return;
    	    }
    	    
    	    if (delayedClick[1] == tick) {
    	    	handler.slots.get(delayedClick[0]).setStack(QUARTZ_BLOCK_STACK.copy());
    	    	delayedClick[0] = -1;
    	    	delayedClick[1] = -1;
    	    }
    	    
    	    int containerSlotCount = handler.slots.size() - client.player.getInventory().size() + 5;
    	    for (int i = 0; i < containerSlotCount; i++) {
    	        Slot slot = handler.slots.get(i);
    	        if (slot.getStack().getItem() == Items.QUARTZ_BLOCK) {
	                client.execute(() -> {
	                    client.interactionManager.clickSlot(handler.syncId, slot.id, 1, SlotActionType.CLONE, client.player);
	                });
	                slot.setStack(DIAMOND_BLOCK_STACK.copy());
	                if (handler.slots.get(i - 9).getStack().getItem().getTranslationKey().contains("wool")) {
	                    delayedClick[0] = i;
	                	delayedClick[1] = tick + 5;
	                }
    	        }
    	    }
    	});
    }

    public static void start(Screen screen) {
    	active = true;
    	currentScreen = screen;
		HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
    	handler = handledScreen.getScreenHandler();
    	ChatUtils.addMessage("§2[SBC] §r§dAutoMelody started");
    }
    
    public static void stop() {
    	if (active) {
            active = false;
            currentScreen = null;
            ChatUtils.addMessage("§2[SBC] §r§dAutoMelody stopped");
        }
	}
}
