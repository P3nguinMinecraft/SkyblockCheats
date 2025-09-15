package com.sbc.feature.hunting;

import com.sbc.command.Debug;
import com.sbc.object.LeashConnection;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.InteractUtils;
import com.sbc.util.TextUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.ArmorStandEntity;

import java.util.ArrayList;

public class Lasso {
    public static volatile boolean blockClick = false;
    private static long lastClick = -1;

    public static void init(){
        Debug.addCommand("lasso-connections", ()-> {
            MinecraftClient client = MinecraftClient.getInstance();
            ArrayList<LeashConnection> connections = getConnections(client);
            ArrayList<LeashConnection> additive = new ArrayList<>();
            for (LeashConnection connection : connections) {
                ChatUtils.sendDebugMessage("Holder: " + connection.getHolder().getName().getString() + " - Leashed: " + connection.getLeashedEntity().getName().getString() + " - Location: " + connection.getLeashedEntity().getPos().toString());
                for (LeashConnection past : additive) {
                    if (past.getLeashedEntity().getPos().distanceTo(connection.getLeashedEntity().getPos()) > 0.5)
                        continue;
                    if (past.getHolder() == client.player || connection.getHolder() == client.player) {
                        ChatUtils.sendDebugMessage("Player is doubling with someone else!");
                        break;
                    }
                    additive.add(connection);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(Lasso::tick);
    }
    private static void tick(MinecraftClient client){
        if (!(boolean) Config.getConfig("lasso-blockreel") && !(boolean) Config.getConfig("lasso-autoreel")) return;
        blockClick = false;
        if (client.world == null || client.player == null) return;
        if (!client.player.getMainHandStack().isOf(net.minecraft.item.Items.LEAD)) return;
        boolean isDeployed = false;
        boolean isDouble = false;
        Entity leashedEntity = null;

        ArrayList<LeashConnection> connections = getConnections(client);
        ArrayList<LeashConnection> additive = new ArrayList<>();
        for (LeashConnection connection : connections) {
            if (connection.getHolder() == client.player){
                isDeployed = true;
                leashedEntity = connection.getLeashedEntity();
            }
            for (LeashConnection past : additive){
                if (past.getLeashedEntity().getPos().distanceTo(connection.getLeashedEntity().getPos()) > 0.5) continue;
                if (past.getHolder() == client.player || connection.getHolder() == client.player){
                    isDouble = true;
                    break;
                }
            }
            additive.add(connection);
        }

        if (isDeployed && leashedEntity != null) {
            if (System.currentTimeMillis() - lastClick < 300) {
                blockClick = true;
            }
            else {
                boolean autoClick = false;
                for (Entity entity : client.world.getEntities()) {
                    if (!(entity instanceof ArmorStandEntity)) continue;
                    if (entity.getPos().distanceTo(leashedEntity.getPos().add(0, 2, 0)) > 3) continue;
                    if (TextUtils.getFormattedText(entity.getDisplayName()).contains("§e§lREEL")) {
                        blockClick = false;
                        autoClick = true;
                        break;
                    }
                    if (TextUtils.getFormattedText(entity.getDisplayName()).contains("§l§m")) {
                        blockClick = true;
                        autoClick = false;
                    }
                }
                if (isDouble) {
                    blockClick = false;
                    autoClick = true;
                }

                if ((boolean) Config.getConfig("lasso-autoreel") && autoClick) {
                    InteractUtils.rightClick();
                    clicked();
                }
            }
        }
    }

    private static ArrayList<LeashConnection> getConnections(MinecraftClient client){
        ArrayList<LeashConnection> connections = new ArrayList<>();
        for (Entity entity : client.world.getEntities()){
            if (!(entity instanceof Leashable leashedEntity)) continue;
            Entity holder = leashedEntity.getLeashHolder();
            if (holder == null) continue;
            connections.add(new LeashConnection(holder, entity));
        }
        return connections;
    }

    public static void clicked(){
        lastClick = System.currentTimeMillis();
    }
}
