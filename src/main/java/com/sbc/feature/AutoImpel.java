package com.sbc.feature;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sbc.util.CameraUtils;
import com.sbc.util.Config;
import com.sbc.util.DelayUtils;
import com.sbc.util.InteractUtils;
import com.sbc.util.ListenerManager;
import com.sbc.util.PlayerCamera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AutoImpel {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Pattern pattern = Pattern.compile("Impel:\\s+(?:§.\\s*)*([A-Z]+(?:\\s+[A-Z]+)*)\\s+(?:§.\\s*)*(\\d+(?:\\.\\d+)?)s");
    private static Consumer<Text> callback;

    private static float lastSubtitleTime = -1f; // last unique subtitle time
    private static long lastAttemptWallTime = 0L; // to control retry rate

    public static void init() {
        callback = subtitle -> {
            if (!Boolean.TRUE.equals(Config.getConfig("auto-impel"))) return;
            String input = subtitle.getString();
            Matcher matcher = pattern.matcher(input);

            if (!matcher.find()) return;

            String action = matcher.group(1);
            float subtitleTime = Float.parseFloat(matcher.group(2));
            handleSubtitle(action, subtitleTime);
        };

        ListenerManager.registerSubtitleListener(callback);
    }
    
    public static void handleSubtitle(String action, float subtitleTime) {
        float rate = ((Number) Config.getConfig("impel-rate")).floatValue();
        float delay = ((Number) Config.getConfig("impel-delay")).floatValue();

        long now = System.currentTimeMillis();

        // Only allow retry every 'rate' seconds
        if ((now - lastAttemptWallTime) < rate * 1000) return;

        // If same subtitleTime, it's a retry; otherwise it's a new subtitle
        boolean isNewSubtitle = Math.abs(subtitleTime - lastSubtitleTime) > 0.001f;
        if (isNewSubtitle) lastSubtitleTime = subtitleTime;

        // Block early if retrying failed actions like JUMP mid-air
        if ("JUMP".equals(action) && (client.player == null || !client.player.isOnGround())) {
            lastAttemptWallTime = now;
            return;
        }

        // Passed all checks; trigger action
        lastAttemptWallTime = now;

        if (delay < (subtitleTime - 0.1f)) {
            DelayUtils.ms((int) (delay * 1000), () -> impel(action));
        } else {
            impel(action);
        }
    }

    private static void impel(String action) {
        switch (action) {
            case "JUMP" -> jump();
            case "CLICK UP" -> clickUp();
            case "CLICK DOWN" -> clickDown();
            case "SNEAK" -> sneak();
        }
    }

    public static void jump() {
        InteractUtils.jump();
    }

    public static void clickUp() {
        float pitch = PlayerCamera.getPitch();
        DelayUtils.tick(0, () -> {
        	CameraUtils.freezeCamera();
	        PlayerCamera.setPitch(-90.0f);
        });
        DelayUtils.tick(1, InteractUtils::leftClick);
        DelayUtils.tick(2, () -> {
	        PlayerCamera.setPitch(pitch);
	        CameraUtils.unfreezeCamera();
        });
    }

    public static void clickDown() {
        float pitch = PlayerCamera.getPitch();
        DelayUtils.tick(0, () -> {
        	CameraUtils.freezeCamera();
	        PlayerCamera.setPitch(90.0f);
        });
        DelayUtils.tick(1, InteractUtils::leftClick);
        DelayUtils.tick(2, () -> {
	        PlayerCamera.setPitch(pitch);
	        CameraUtils.unfreezeCamera();
        });
    }

    public static void sneak() {
        DelayUtils.tick(0, InteractUtils::sneak);
        DelayUtils.tick(1, InteractUtils::sneak);
    }
}
