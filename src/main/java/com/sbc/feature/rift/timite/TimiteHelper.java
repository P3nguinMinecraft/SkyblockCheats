package com.sbc.feature.rift.timite;

import com.sbc.feature.rift.timite.object.OreCount;
import com.sbc.feature.rift.timite.object.OreTracker;
import com.sbc.feature.rift.timite.object.OreType;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.InventoryUtils;
import com.sbc.util.ScoreboardUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static com.sbc.feature.rift.timite.object.OreType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimiteHelper {
    private static final long DECAY_TIME = 15;
    private static long clientTick = 0;

    public static final ArrayList<OreTracker> trackers = new ArrayList<>();

    private static OreCount youngiteCount;
    private static OreCount timiteCount;
    private static OreCount obsoliteCount;

    public static HelperState status = HelperState.OFF;

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(TimiteHelper::tick);
        youngiteCount = new OreCount(YOUNGITE, 0, (int) Config.getConfig("youngite-ratio"));
        timiteCount = new OreCount(OreType.TIMITE, 0, (int) Config.getConfig("timite-ratio"));
        obsoliteCount = new OreCount(OreType.OBSOLITE, 0, (int) Config.getConfig("obsolite-ratio"));
    }


    public static void tick(MinecraftClient client){
        clientTick++;
        if (client.world == null) return;

        trackers.removeIf(tracker -> {
            if (!client.world.getBlockState(tracker.pos).isOf(tracker.type.block)) {
                return true;
            }
            if (clientTick - tracker.lastAged > DECAY_TIME){
                return true;
            }
            return false;
        });
        trackers.forEach(OreTracker::tick);

        if (!(boolean) Config.getConfig("timite-helper")){
            reset(client);
            return;
        }
        
        HitResult hit = client.crosshairTarget;
        if (!ScoreboardUtils.contains("The Mountaintop") || !(hit instanceof BlockHitResult blockhit)){
            reset(client);
            return;
        }

        BlockPos pos = blockhit.getBlockPos();
        BlockState state = client.world.getBlockState(pos);
        Block block = state.getBlock();

        if (OreType.getType(block).stage == 0){
            reset(client);
            return;
        }

        OreType priority = getPriority();
        int floorStage = priority.stage;

        OreTracker tracker = getTracker(pos);
        if (tracker == null){
            OreType type = OreType.getType(block);
            if (type.stage >= floorStage){
                status = HelperState.MINING;
            }
            else {
                trackers.add(new OreTracker(pos, type, clientTick));
                status = HelperState.AGING;
            }
        }
        else {
            if (tracker.type.stage >= floorStage){
                status = HelperState.MINING;
                trackers.remove(tracker);
            }
            else status = HelperState.AGING;
        }

        int[] slots = getSlots();
        if (slots[0] == -1 || slots[1] == -1){
            reset(client);
            return;
        }

        if (status == HelperState.AGING){
            client.options.attackKey.setPressed(false);
            InventoryUtils.setSlot(slots[0]);
            tracker = getTracker(pos);
            tracker.attemptAge(clientTick);
        }
        else if (status == HelperState.MINING) {
            if (client.player == null || client.currentScreen != null){
                reset(client);
                return;
            }
            InventoryUtils.setSlot(slots[1]);
            client.options.attackKey.setPressed(true);
        }
        else if (status == HelperState.OFF) {
            reset(client);
        }

    }

    @Nullable
    private static OreTracker getTracker(BlockPos pos){
        for (OreTracker tracker : trackers){
            if (tracker.pos.equals(pos)) return tracker;
        }
        return null;
    }

    private static OreType getPriority(){
        youngiteCount.update(InventoryUtils.countItems("YOUNGITE"), (int) Config.getConfig("youngite-ratio"));
        timiteCount.update(InventoryUtils.countItems("TIMITE"), (int) Config.getConfig("timite-ratio"));
        obsoliteCount.update(InventoryUtils.countItems("OBSOLITE"), (int) Config.getConfig("obsolite-ratio"));

        List<Integer> sets = Arrays.asList(youngiteCount.sets, timiteCount.sets, obsoliteCount.sets);
        Collections.sort(sets);
        if (youngiteCount.sets == sets.get(0)){
            return YOUNGITE;
        }
        else if (timiteCount.sets == sets.get(0)) {
            return TIMITE;
        }
        else {
            return OBSOLITE;
        }
    }

    private static int[] getSlots(){
        ArrayList<Integer> gunSlots = InventoryUtils.getItemSlots("TIME_GUN");
        int gunSlot = gunSlots.isEmpty() ? -1: gunSlots.get(0) > 8 ? -1 : gunSlots.get(0);
        ArrayList<Integer> pickaxeSlots = InventoryUtils.getItemSlots("CHRONO_PICKAXE");
        int pickaxeSlot = pickaxeSlots.isEmpty() ? -1 : pickaxeSlots.get(0) > 8 ? -1 : pickaxeSlots.get(0);

        if (gunSlot == -1){
            Config.setConfig("timite-helper", false);
            ChatUtils.addMessage("§2[SBC§r§3-TH§r§2] §cDid not find §l§eTime Gun§r §cin your hotbar. Config set to false!");
        }
        if (pickaxeSlot == -1){
            Config.setConfig("timite-helper", false);
            ChatUtils.addMessage("§2[SBC§r§3-TH§r§2] §cDid not find §l§eChrono Pickaxe§r §cin your hotbar. Config set to false!");
        }
        int[] slots = new int[2];
        slots[0] = gunSlot;
        slots[1] = pickaxeSlot;
        return slots;
    }

    private static void reset(MinecraftClient client){
        if (status != HelperState.OFF) {
            status = HelperState.OFF;
            client.interactionManager.cancelBlockBreaking();
            client.options.attackKey.setPressed(false);
        }
    }

    public enum HelperState {
        AGING,
        MINING,
        OFF
    }
}
