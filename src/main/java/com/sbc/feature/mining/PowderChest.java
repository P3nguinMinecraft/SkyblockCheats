package com.sbc.feature.mining;

import com.sbc.util.Config;
import com.sbc.util.InteractUtils;
import com.sbc.util.InventoryUtils;
import com.sbc.util.Skyblock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PowderChest {
    private static final int CLICK_DELAY = 4;
    private static volatile boolean cleared = false;
    private static int original = 0;
    public static final HashSet<BlockPos> chests = new HashSet<>();
    public static final HashMap<BlockPos, Integer> clicked = new HashMap<>();
    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(PowderChest::tick);
    }
    public static void tick(MinecraftClient client){
        cleared = false;
        Iterator<HashMap.Entry<BlockPos, Integer>> iter = clicked.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<BlockPos, Integer> entry = iter.next();
            BlockPos pos = entry.getKey();
            int delay = entry.getValue();

            if (isOpen(pos, client)) {
                cleared = true;
                iter.remove();
            } else if (delay > 0) {
                entry.setValue(delay - 1);
            } else {
                cleared = true;
                iter.remove();
            }
        }
        if (!Skyblock.inCH || !(boolean) Config.getConfig("auto-open-powder")) return;
        if (client.player == null || client.world == null) return;
        if (clicked.isEmpty() && cleared){
            InventoryUtils.setSlot(original);
        }

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockhit)) return;
        BlockPos pos = blockhit.getBlockPos();
        BlockState state = client.world.getBlockState(pos);

        if (!state.isOf(Blocks.CHEST)) return;
        if (!isOpen(pos, client)){
            attemptClick(pos, client);
        }
    }

    private static void attemptClick(BlockPos pos, MinecraftClient client) {
        if (clicked.get(pos) != null){
            return;
        }
        ArrayList<Integer> slots = InventoryUtils.getItemSlotsLoreContains("% Gemstone Powder");
        if (clicked.isEmpty()) original = InventoryUtils.currentSlot();
        if (!slots.isEmpty() && slots.get(0) < 9) InventoryUtils.setSlot(slots.get(0));
        InteractUtils.rightClick();
        clicked.put(pos, CLICK_DELAY);
    }

    public static boolean isOpen(BlockPos pos, MinecraftClient client) {
        BlockEntity bEntity = client.world.getBlockEntity(pos);
        if (!(bEntity instanceof ChestBlockEntity entity)) return false;
        float angle = entity.getAnimationProgress(1.0f);
        return angle > 0.0f;
    }
}
