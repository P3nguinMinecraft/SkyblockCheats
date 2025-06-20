package com.sbc.util;

import com.sbc.accessor.ICameraAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

public class CameraUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    
    public static float fyaw = 0.0f;
    public static float fpitch = 0.0f;
    public static boolean freeze = false;

    public static void setCameraRotation(float yaw, float pitch) {
        System.out.println("Trying to rotate camera to yaw=" + yaw + ", pitch=" + pitch);
        Camera camera = client.gameRenderer.getCamera();
        ((ICameraAccessor) camera).invokeSetRotation(yaw, pitch);
    }
    
    public static void freezeCamera() {
		freeze = true;
		fyaw = client.gameRenderer.getCamera().getYaw();
		fpitch = client.gameRenderer.getCamera().getPitch();
	}
    
    public static void unfreezeCamera() {
    	freeze = false;
    }
}
