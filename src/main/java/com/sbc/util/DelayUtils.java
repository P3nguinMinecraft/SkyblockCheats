package com.sbc.util;

import java.util.LinkedList;
import java.util.Queue;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class DelayUtils {
    private static final Queue<TickTask> tickTasks = new LinkedList<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.world == null) return;

            tickTasks.removeIf(task -> {
                if (task.ticksRemaining <= 0) {
                    task.runnable.run();
                    return true;
                }
                task.ticksRemaining--;
                return false;
            });
        });
    }

    public static void tick(int ticks, Runnable task) {
        tickTasks.add(new TickTask(ticks, task));
    }

    public static void ms(int ms, Runnable task) {
        new Thread(() -> {
            try {
                Thread.sleep(ms);
                MinecraftClient.getInstance().execute(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class TickTask {
        int ticksRemaining;
        Runnable runnable;

        TickTask(int ticks, Runnable runnable) {
            this.ticksRemaining = ticks;
            this.runnable = runnable;
        }
    }
}
