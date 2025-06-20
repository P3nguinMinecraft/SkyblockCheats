package com.sbc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class PlayerCamera {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void lookBlock(int x, int y, int z) {
        lookPos(x + 0.5F, y + 0.5F, z + 0.5F);
    }

	public static void lookPos(Vec3d vec){
		lookPos(vec.x, vec.y, vec.z);
	}

	public static void lookPos(double x, double y, double z){
		lookPos((float) x, (float) y, (float) z);
	}

    public static void lookPos(float x, float y, float z) {
		if (client.player == null) return;

		Vec3d playerPos = client.player.getCameraPosVec(1.0F);
		Vec3d targetPos = new Vec3d(x, y, z);

		Vec3d direction = targetPos.subtract(playerPos);

		double dx = direction.x;
		double dy = direction.y;
		double dz = direction.z;

		double distanceXZ = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(dy, distanceXZ));

		setYaw(yaw);
		setPitch(pitch);
	}
    
    public static void setYaw(float yaw) {
    	client.player.setYaw(yaw);
    }
    
    public static float getYaw() {
		return client.player.getYaw();
	}
    
    public static void setPitch(float pitch) {
		client.player.setPitch(pitch);
	}
    
    public static float getPitch() {
		return client.player.getPitch();
	}
}
