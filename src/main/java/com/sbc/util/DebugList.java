package com.sbc.util;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import com.mojang.serialization.JsonOps;
import com.sbc.command.Debug;
import config.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class DebugList {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final LinkedList<ScheduledCommand> queue = new LinkedList<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(DebugList::tick);
        Debug.addCommand("getscoreboard", () -> {
            ChatUtils.sendDebugMessage(ScoreboardUtils.getString());
        });
        Debug.addCommand("getcustomnbt", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            NbtCompound compound = ItemUtils.getCustomData(stack);
            ChatUtils.addMessage(compound.toString());
        });
        Debug.addCommand("getid", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            String id = ItemUtils.getId(stack);
            ChatUtils.addMessage(id != null ? id : "null");
        });
        Debug.addCommand("getenchants", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            HashMap<String, Integer> enchants = ItemUtils.getEnchants(stack);
            if (enchants == null || enchants.isEmpty()) {
                ChatUtils.addMessage("null");
            } else {
                String out = enchants.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(", ")   );
                ChatUtils.addMessage(out);
            }
        });
        Debug.addCommand("getname", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            Text text = stack.getFormattedName();
            String json = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).toString();
            ChatUtils.sendDebugMessage(json);
            ChatUtils.addMessage("Color: " + TextUtils.getNestedColor(text));
        });
        Debug.addCommand("getloreformatted", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            List<Text> lore = ItemUtils.getLore(stack);

            for (Text text : lore) {
                ChatUtils.addMessage(text);
                ChatUtils.addMessage(TextUtils.toString(text));
            }
        });
        Debug.addCommand("getlore", () -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            String lore = ItemUtils.getLoreAsString(stack);
            ChatUtils.addMessage(lore);
        });
        Debug.addCommand("findgun", () -> {
            ArrayList<Integer> gunSlots = InventoryUtils.getItemSlots("TIME_GUN");
            int gunSlot = gunSlots.isEmpty() ? -1: gunSlots.get(0) > 8 ? -1 : gunSlots.get(0);
            if (gunSlot == -1){
                ChatUtils.sendDebugMessage("Did not find Time Gun in your hotbar.");
            }
            else {
                ChatUtils.sendDebugMessage("Found Time Gun in slot " + gunSlot);
            }
        });
        Debug.addCommand("findpick", () -> {
            ArrayList<Integer> pickaxeSlots = InventoryUtils.getItemSlots("CHRONO_PICKAXE");
            int pickaxeSlot = pickaxeSlots.isEmpty() ? -1 : pickaxeSlots.get(0) > 8 ? -1 : pickaxeSlots.get(0);
            if (pickaxeSlot == -1){
                ChatUtils.addMessage("Did not find Chrono Pickaxe in your hotbar.");
            }
            else {
                ChatUtils.sendDebugMessage("Found Chrono Pickaxe in slot " + pickaxeSlot);
            }
        });

//        Debug.addCommand("mine", () -> {
//            queue.add(new ScheduledCommand(() -> {
//                client.execute(() -> {
//                    HitResult hit = client.crosshairTarget;
//                    if (!(hit instanceof BlockHitResult blockhit)) return;
//                    BlockPos pos = blockhit.getBlockPos();
//                    client.interactionManager.updateBlockBreakingProgress(pos, blockhit.getSide());
//                    client.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
//                    client.player.swingHand(Hand.MAIN_HAND, true);
//                });
//            }, 20, 1));
//        });

        Debug.addCommand("mine", () -> {
            queue.add(new ScheduledCommand(() -> {
                client.options.attackKey.setPressed(true);
            }, 20, 1));
            queue.add(new ScheduledCommand(() -> {
                client.options.attackKey.setPressed(false);
            }, 21, 20));
        });

        Debug.addCommand("config", Config.INSTANCE::openGui);
    }

    private static void tick(MinecraftClient minecraftClient) {
        Iterator<ScheduledCommand> iterator = queue.iterator();
        while (iterator.hasNext()) {
            ScheduledCommand task = iterator.next();
            if (task.tick()) iterator.remove();
        }
    }

    private static final class ScheduledCommand {
        final Runnable command;
        int remainingTicks;
        final int interval;
        final int totalTicks;
        int tickCounter = 0;

        ScheduledCommand(Runnable command, int totalTicks, int interval) {
            this.command = command;
            this.totalTicks = totalTicks;
            this.interval = interval;
            this.remainingTicks = totalTicks;
        }

        boolean tick() {
            if (remainingTicks <= 0) return true; // Remove when done
            if (tickCounter % interval == 0) command.run();
            tickCounter++;
            remainingTicks--;
            return remainingTicks <= 0;
        }
    }


}
