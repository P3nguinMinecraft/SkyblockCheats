package com.sbc.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.sbc.command.Debug;
import com.sbc.feature.AutoImpel;

public class DebugList {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void init() {
        Debug.addCommand("jump", () -> AutoImpel.jump());
        Debug.addCommand("sneak", () -> AutoImpel.sneak());
        Debug.addCommand("clickup", () -> AutoImpel.clickUp());
        Debug.addCommand("clickdown", () -> AutoImpel.clickDown());
        Debug.addCommand("runjump", () -> loopCommand(() -> AutoImpel.handleSubtitle("JUMP", 2.0f), 40));
        Debug.addCommand("runsneak", () -> loopCommand(() -> AutoImpel.handleSubtitle("SNEAK", 2.0f), 40));
        Debug.addCommand("runclickup", () -> loopCommand(() -> AutoImpel.handleSubtitle("CLICK UP", 2.0f), 40));
        Debug.addCommand("runclickdown", () -> loopCommand(() -> AutoImpel.handleSubtitle("CLICK DOWN", 2.0f), 40));
        Debug.addCommand("freeze", () -> CameraUtils.freezeCamera());
        Debug.addCommand("unfreeze", () -> CameraUtils.unfreezeCamera());
    }

    private static void loopCommand(Runnable command, int ticks) {
        final int[] tickCounter = {0};
        final ScheduledFuture<?>[] task = new ScheduledFuture<?>[1];
        task[0] = scheduler.scheduleAtFixedRate(() -> {
            if (tickCounter[0] >= ticks) {
                task[0].cancel(false);
                return;
            }
            command.run();
            tickCounter[0]++;
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

}
