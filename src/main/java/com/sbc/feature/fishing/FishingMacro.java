package com.sbc.feature.fishing;

import com.sbc.util.Config;
import com.sbc.util.DelayUtils;
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
    private static int cd1 = 0;
    private static int cd2 = 0;
    private static int timeSince = 0;

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(FishingMacro::tick);
    }

    public static void tick(MinecraftClient client){
        cd1 = Math.max(cd1 - 1, 0);
        cd2 = Math.max(cd2 - 1, 0);
        timeSince = Math.max(timeSince + 1, 500);
        if (!(boolean) Config.getConfig("fishing-macro") && !(boolean) Config.getConfig("fishing-autoreel")) return;
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
            if (((boolean) Config.getConfig("fishing-macro") || (boolean) Config.getConfig("fishing-autoreel")) && cd1 <= 0){
                if (!(boolean) Config.getConfig("slugfish-toggle") || timeSince > 400) {
                    DelayUtils.tick(0, InteractUtils::rightClick);
                    cd1 = 10;
                    cd2 = 2;
                }
            }
        }
        else {
            if ((boolean) Config.getConfig("fishing-macro")){
                if (client.player.fishHook == null && cd2 <= 0){
                    DelayUtils.tick(0, InteractUtils::rightClick);
                    cd2 = 10;
                }
            }
        }
    }

    public static void rodCast(World world, PlayerEntity user, Hand hand){
        if (!user.equals(MinecraftClient.getInstance().player)) return;
        clearStands();
        timeSince = 0;
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
