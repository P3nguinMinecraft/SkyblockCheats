package com.sbc.feature.skyblock.beachball;

import java.util.*;

import com.sbc.data.Constants;
import com.sbc.data.Textures;
import com.sbc.object.Color;
import com.sbc.render.RenderHelper;
import com.sbc.util.*;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class BeachBall {
    private static final walkMethod method = walkMethod.QUAD_DIRECTION;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Map<Integer, Predictor> predictors = new HashMap<>();
    private static final Set<Integer> ids = new HashSet<>();
    private static int bounces = -1;
    private static Vec3d walkTarget;
    private static BallState state = BallState.BOUNCING;
    public static boolean activated = false;
    private static boolean tempOverwrite = false;

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(BeachBall::render);
        ClientTickEvents.END_CLIENT_TICK.register(BeachBall::tick);
        ListenerManager.registerOverlayListener((t) -> {
            String message = t.toString();
            bounces = parseBounces(message);
            if (bounces > 0 && bounces < 40) tempOverwrite = false;
            if (bounces >= 40 && tempOverwrite){
                bounces = -1;
            }
        });
        ListenerManager.registerMessageListener((s) -> {
            if (s.contains("in the air for") || s.contains("hit the wall after")){
                activated = true;
                if (state == BallState.BOUNCING){
                    if ((boolean) Config.getConfig("fullauto-beachball")){
                        state = BallState.GO_TO_CENTER;
                    }
                }
                if (state == BallState.GO_TO_CENTER) {
                    state = BallState.WAIT_FOR_LAND_AND_RESTART;
                }
            }
        });

        new Thread(BeachBall::walkTo).start();
    }

    private static int parseBounces(String message){
            String key = "Bounces:";
            int index = message.indexOf(key);
            if (index == -1) return -1;

            String after = message.substring(index + key.length()).trim();
            after = after.replaceAll("§.", "");

            String[] parts = after.split("\\s+");
            if (parts.length == 0) return -1;

            try {
                return Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return -1;
            }
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null) return;
        predictors.entrySet().removeIf(entry -> {
            if (!(boolean) Config.getConfig("beachball-predictor")) return true;
            Entity entity = client.world.getEntityById(entry.getKey());
            //if (!(entity instanceof ArrowEntity armorStand)) return true;
            if (!(entity instanceof ArmorStandEntity armorStand)) return true;
            entry.getValue().update(client.world.getTimeOfDay(), armorStand.getPos());
            return false;
        });
        if (!(boolean) Config.getConfig("beachball-predictor")) return;
	    for (Entity entity : client.world.getEntities()) {
	        checkEntity(entity);
	    }
    }

    public static void checkEntity(Entity entity) {
        if (ids.contains(entity.getId())) return;
        ids.add(entity.getId());
        //if (!(entity instanceof ArrowEntity)) return;

        if (!(entity instanceof ArmorStandEntity stand)) return;
        ItemStack stack = stand.getEquippedStack(EquipmentSlot.HEAD);
        if (stack.isEmpty() || !stack.isOf(Items.PLAYER_HEAD) || !ItemUtils.getHeadTexture(stack).equals(Textures.NORMAL_BEACH_BALL)) return;

        predictors.putIfAbsent(entity.getId(), new Predictor(client.world.getTimeOfDay(), entity.getPos()));
    }

    public static void render(WorldRenderContext context) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        Vec3d playerFeet = player.getPos();
        Vec3d playerPos = playerFeet.add(0, 1, 0);

        for (Map.Entry<Integer, Predictor> entry : predictors.entrySet()) {
            Predictor predictor = entry.getValue();
            Vec3d landing = predictor.getLandingPos(playerPos.y);
            if (landing != null) {
                float dist = (float) landing.distanceTo(playerPos);
                Color color = dist < 0.3 ? new Color(0f, 1f, 0.1f, 1f) : dist < 0.9 ? new Color(1f, 0.8f, 0.2f, 1.0f) : new Color(1f, 0.24f, 0.24f, 1f);

                Vec3d c1 = new Vec3d(landing.x - 0.3, landing.y - 0.3, landing.z - 0.3);
                Vec3d c2 = new Vec3d(landing.x + 0.3, landing.y + 0.3, landing.z + 0.3);

                RenderHelper.renderFilled(context, c1, c2, color, true);
            }

            Vec3d[] points = predictor.getPath().toArray(new Vec3d[0]);
            RenderHelper.renderLinesFromPoints(context, points, new Color(1f, 1f, 1f, 1f), 2.0f, false);
        }
    }

    @Nullable
    private static Vec3d getTarget(){
        if (predictors.isEmpty()) return null;
        int id = -1;
        double closestDist = 5;
        for (int e : predictors.keySet()){
            Predictor p = predictors.get(e);
            if (p.originalDistance < closestDist){
                closestDist = p.originalDistance;
                id = e;
            }
        }
        if (id != -1){
            Vec3d pos = client.world.getEntityById(id).getPos();
            return new Vec3d(pos.x, client.player.getPos().y, pos.z);
        }
        return null;
    }

    private static void walkTo(){
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            client.execute(() -> {
                if (!(Boolean) Config.getConfig("auto-beachball") || client.world == null || client.player == null) return;

                switch (state) {
                    case BOUNCING -> {
                        if (bounces >= 40) {
                            if ((boolean) Config.getConfig("fullauto-beachball")){
                                state = BallState.GO_TO_CENTER;
                            }
                        }
                        else {
                            walkTarget = getTarget();
                        }
                    }

                    case GO_TO_CENTER -> {
                        walkTarget = Constants.DUNGEON_HUB_BALL;
                        if (client.player.getPos().distanceTo(walkTarget) < 3) {
                            KeyboardUtils.reset();
                            state = BallState.WAIT_FOR_LAND_AND_RESTART;
                        }
                    }

                    case WAIT_FOR_LAND_AND_RESTART -> {
                        if (activated) {
                            predictors.clear();
                            ids.clear();
                            activated = false;
                            bounces = 0;
                            tempOverwrite = true;
                            InteractUtils.rightClick();
                            state = BallState.BOUNCING;
                        }
                    }
                }

                if (walkTarget != null && state != BallState.WAIT_FOR_LAND_AND_RESTART) {
                    double dx = walkTarget.x - client.player.getX();
                    double dz = walkTarget.z - client.player.getZ();

                    KeyboardUtils.sneaking = walkTarget.distanceTo(client.player.getPos()) < 0.3;

                    if (method == walkMethod.LOOK) {
                        if (walkTarget.distanceTo(client.player.getPos()) > MovementUtils.getStopDistance() + 0.2) {
                            PlayerCamera.lookPos(walkTarget);
                            KeyboardUtils.walkForward = true;
                        } else {
                            KeyboardUtils.walkForward = false;
                        }
                    }

                    if (method == walkMethod.QUAD_DIRECTION) {
                        PlayerCamera.setYaw(0f);
                        KeyboardUtils.walkLeft = dx > 0.05;
                        KeyboardUtils.walkRight = dx < -0.05;
                        KeyboardUtils.walkForward = dz > 0.05;
                        KeyboardUtils.walkBack = dz < -0.05;
                    }
                } else {
                    KeyboardUtils.reset();
                }
            });
        }
    }

    public enum walkMethod {
        LOOK,
        QUAD_DIRECTION
    }

    public enum BallState {
        BOUNCING,
        GO_TO_CENTER,
        WAIT_FOR_LAND_AND_RESTART
    }

}
