package com.sbc.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sbc.task.ScanTask;
import com.sbc.util.ChatUtils;
import com.sbc.util.ConfigManager;
import com.sbc.render.Render;
import com.sbc.render.Render.RenderMode;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class SearchManager {
    private static volatile boolean active = false;
    private static volatile boolean found = false;
    private static BlockPos foundPos = null;
    private static ScanTask scanTask = null;
    private static Thread searchThread = null;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static synchronized void toggleSearch() {
        if (active) {
        	active = false;
            clearSearch();
            ChatUtils.sendMessage("§cSearch stopped.");
        } 
        else {
            active = true;
            found = false;
           	if (foundPos != null) {
            	ChatUtils.sendMessage("§eSearch cleared.");
           		Render.removeBlock(foundPos);
           		foundPos = null;
           	}
            ChatUtils.sendMessage("§aSearch started...");
            runSearchLoop();
        }
    }

    public static synchronized void clearSearch() {
        found = false;

        if (scanTask != null) {
            scanTask.cancel();
            scanTask = null;
        }

        if (searchThread != null) {
            searchThread.interrupt();
            searchThread = null;
        }
        
       	if (foundPos != null) {
        	ChatUtils.sendMessage("§eSearch cleared.");
       		Render.removeBlock(foundPos);
       		foundPos = null;
       	}
    }

    private static void runSearchLoop() {
        searchThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && active && !found) {
                    runCommand("/warp forge");

                    if (!waitForProfileOrDelay(0)) break; // 1 for testing! set to 0 when compiling
                    if (!active) break;

                    runCommand("/warp ch");

                    sleepInterruptibly(2000);
                    if (!active) break;

                    AtomicBoolean foundInTask = new AtomicBoolean(false);

                    ScanTask glassScanTask = new ScanTask(pos -> {
                        foundInTask.set(true);
                        setFoundBlock(pos);
                        double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2) + Math.pow(client.player.getY() - pos.getY(), 2) + Math.pow(client.player.getZ() - pos.getZ(), 2));
                        double roundedDistance = Math.round(distance * 10.0) / 10.0;
                        ChatUtils.sendMessage("§aFound Magenta Stained Glass at §rx: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ() + "\n§dDistance: §r" + roundedDistance);
                        String rgba = (String) ConfigManager.getConfig("rgbaBlockColor");
                        float r = 255, g = 0, b = 0, a = 1.0f;
                        if (rgba != null && !rgba.isEmpty()) {
                            String[] parts = rgba.split(",");
                            if (parts.length >= 3) {
                                try {
                                    r = Float.parseFloat(parts[0].trim());
                                    g = Float.parseFloat(parts[1].trim());
                                    b = Float.parseFloat(parts[2].trim());
                                    if (parts.length >= 4) {
                                        a = Float.parseFloat(parts[3].trim());
                                    }
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                        Render.addBlock(pos, new ArrayList<Float>(List.of(r, g, b, a)), (boolean) ConfigManager.getConfig("fullHighlight") ? RenderMode.HIGHLIGHT : RenderMode.OUTLINE);
                        active = false;
                    }, pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS));

                    ScanTask paneScanTask = new ScanTask(pos -> {
                        foundInTask.set(true);
                        setFoundBlock(pos);
                        double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2) + Math.pow(client.player.getY() - pos.getY(), 2) + Math.pow(client.player.getZ() - pos.getZ(), 2));
                        double roundedDistance = Math.round(distance * 10.0) / 10.0;
                        ChatUtils.sendMessage("§aFound Magenta Stained Glass Pane at §rx: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ() + "\n§dDistance: §r" + roundedDistance);
                        String rgba = (String) ConfigManager.getConfig("rgbaBlockColor");
                        float r = 255, g = 0, b = 0, a = 1.0f;
                        if (rgba != null && !rgba.isEmpty()) {
                            String[] parts = rgba.split(",");
                            if (parts.length >= 3) {
                                try {
                                    r = Float.parseFloat(parts[0].trim());
                                    g = Float.parseFloat(parts[1].trim());
                                    b = Float.parseFloat(parts[2].trim());
                                    if (parts.length >= 4) {
                                        a = Float.parseFloat(parts[3].trim());
                                    }
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                        Render.addBlock(pos, new ArrayList<Float>(List.of(r, g, b, a)), (boolean) ConfigManager.getConfig("fullHighlight") ? RenderMode.HIGHLIGHT : RenderMode.OUTLINE);
                        active = false;
                    }, pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS_PANE));

                    glassScanTask.start();
                    if (!active) break;
                    paneScanTask.start();

                    int timeout = 10_000;
                    long startTime = System.currentTimeMillis();
                    while (!Thread.currentThread().isInterrupted() && active && !foundInTask.get() && (System.currentTimeMillis() - startTime) < timeout) {
                        sleepInterruptibly(100);
                    }

                    if (!foundInTask.get()) {
                        if (!waitForProfileOrDelay((int) ConfigManager.getConfig("delay"))) break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SearchLoopThread");

        searchThread.start();
    }

    
    private static boolean waitForProfileOrDelay(int seconds) throws InterruptedException {
        if (seconds > 0) {
            sleepInterruptibly(seconds * 1000);
            return true;
        } else {
            return ChatUtils.waitForChatMessage("You are playing on profile:", 10000);
        }
    }

    private static void runCommand(String command) {
        if (client.player != null) {
            client.execute(() -> client.player.networkHandler.sendChatMessage(command));
        }
    }

    private static void sleepInterruptibly(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    public static BlockPos getFoundPos() {
        return foundPos;
    }

    private static synchronized void setFoundBlock(BlockPos pos) {
        found = true;
        foundPos = pos;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isFound() {
        return found;
    }
}