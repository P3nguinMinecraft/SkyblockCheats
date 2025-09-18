package com.sbc.feature.fishing;

import com.sbc.util.Config;
import com.sbc.util.InteractUtils;
import com.sbc.util.TextUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;

public class FishingMacro {
    private static final ArrayList<ArmorStandEntity> potentialArmorStands = new ArrayList<>();
    private static ArmorStandEntity armorStand = null;
    private static long lastTick = 0;

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(FishingMacro::tick);
    }

    public static void tick(MinecraftClient client){
        lastTick++;
        if (!(boolean) Config.getConfig("fishing-macro")) return;
        if (client.player == null || client.world == null) return;
        if (!client.player.getMainHandStack().getItem().asItem().equals(Items.FISHING_ROD)){
            clearStands();
            return;
        }
        if (armorStand != null && (armorStand.isRemoved() || armorStand.isDead())){
            clearStands();
        }

        if (armorStand == null) {
            for (Entity entity : client.world.getEntities()) {
                if (!(entity instanceof ArmorStandEntity stand)) continue;
                if (potentialArmorStands.contains(stand)) continue;
                if (stand.distanceTo(client.player) < 40){
                    potentialArmorStands.add(stand);
                }
            }
            potentialArmorStands.stream().filter(stand -> stand.hasCustomName() && hasCorrectName(stand)).findFirst().ifPresent(foundStand -> {
                armorStand = foundStand;
            });
        }

        if (armorStand != null && TextUtils.getFormattedText(armorStand.getCustomName()).equals("§c§l!!!§r")){
            if ((boolean) Config.getConfig("fishing-macro")){
                if (lastTick > 5) {
                    InteractUtils.rightClick();
                    lastTick = 0;
                }
            }
        }
        else {
            if ((boolean) Config.getConfig("fishing-macro")){
                if (client.player.fishHook == null && lastTick > 5){
                    InteractUtils.rightClick();
                    lastTick = 0;
                }
            }
        }
    }

    public static void rodCast(World world, PlayerEntity user, Hand hand){
        if (!user.equals(MinecraftClient.getInstance().player)) return;
        clearStands();
    }

    private static void clearStands(){
        armorStand = null;
        potentialArmorStands.clear();
    }

    private static boolean hasCorrectName(ArmorStandEntity stand){
        String name = TextUtils.getFormattedText(stand.getCustomName());
        return name.equals("§c§l!!!§r") || name.matches("§e§l(\\d+(\\.\\d+)?)§r");
    }
}
