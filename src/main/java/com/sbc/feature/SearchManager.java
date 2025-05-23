package com.sbc.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sbc.render.Render;
import com.sbc.render.Render.RenderMode;
import com.sbc.task.ScanTask;
import com.sbc.util.ChatUtils;
import com.sbc.util.ConfigManager;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class SearchManager {
    public static ArrayList<BlockPos> foundBlocks = new ArrayList<>();
    public static volatile boolean loopActive = false;
    public static volatile boolean active = false;
    private static volatile boolean found = false;
    private static Thread searchLoopThread = null;
    private static Thread manualScanTaskThread = null;
    private static Thread scanTaskThread = null;
    private static ScanTask glassScanTask, paneScanTask;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static synchronized void toggleSearch() {
        if (loopActive) {
            clearSearch();
            ChatUtils.sendMessage("§cSearch stopped.");
        }
        else {
           	if (found) {
                found = false;
            	ChatUtils.sendMessage("§eSearch cleared.");
            	for (BlockPos pos : foundBlocks) {
					Render.removeBlock(pos);
				}
                foundBlocks.clear();
           	}
            ChatUtils.sendMessage("§aSearch started...");
            runSearchLoop();
        }
    }

    public static synchronized void clearSearch() {
    	loopActive = false;
    	active = false;

        if (searchLoopThread != null) {
            searchLoopThread.interrupt();
            searchLoopThread = null;
        }

        if (scanTaskThread != null) {
			scanTaskThread.interrupt();
			scanTaskThread = null;
		}

        endTasks();

        if (found) {
            found = false;
        	ChatUtils.sendMessage("§eSearch cleared.");
        	for (BlockPos pos : foundBlocks) {
				Render.removeBlock(pos);
			}
            foundBlocks.clear();
       	}
    }

    public static synchronized void scan() {
    	if (active) {
			active = false;
			ChatUtils.sendMessage("§cScan cancelled.");
		    if (manualScanTaskThread != null) {
				manualScanTaskThread.interrupt();
			    manualScanTaskThread = null;
		    }
		    endTasks();
		    return;
		}

		manualScanTaskThread = new Thread(() -> {
	    	Object lock = new Object();
			runScanTaskAsync(0,
			    () -> {
			    	synchronized (lock) { lock.notify(); }
			    },
			    () -> {
			    	synchronized (lock) { lock.notify(); }
			    }
			);

            synchronized (lock) {
            	try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
	    }, "ManualScanTaskThread");
		manualScanTaskThread.start();
    }

    public static void listSearch() {
    	if (foundBlocks.isEmpty()) {
			ChatUtils.sendMessage("§cNo blocks found.");
		} else {
			StringBuilder message = new StringBuilder("§aFound blocks:§r");
			for (BlockPos pos : foundBlocks) {
				message.append("\n").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ());
			}
			ChatUtils.sendMessage(message.toString());
		}
    }

    private static void runSearchLoop() {
    	loopActive = true;
        searchLoopThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && loopActive && !found) {
                    runCommand("/warp forge");

                    if (!waitForProfileOrDelay(0) || !loopActive) {
						break;
					}

                    runCommand("/warp ch");

                    sleepInterruptibly(2000);
                    if (!loopActive) {
						break;
					}

                    Object lock = new Object();
                    AtomicBoolean foundTask = new AtomicBoolean(false);

                    runScanTaskAsync(10000,
                        () -> {
                        	loopActive = false;
                            foundTask.set(true);
                            synchronized (lock) { lock.notify(); }
                        },
                        () -> {
                        	loopActive = false;
                            synchronized (lock) { lock.notify(); }
                        }
                    );

                    synchronized (lock) {
                        lock.wait();
                    }

                    if (!foundTask.get() && !waitForProfileOrDelay((int) ConfigManager.getConfig("delay"))) {
						break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SearchLoopThread");

        searchLoopThread.start();
    }

    private static void runScanTaskAsync(int timeout, Runnable onFound, Runnable onTimeout) {
        if (active) {
            ChatUtils.sendMessage("§cA scan is already in progress.");
            return;
        }
        active = true;

        if (found) {
            found = false;
            ChatUtils.sendMessage("§eSearch cleared.");
            for (BlockPos pos : foundBlocks) {
                Render.removeBlock(pos);
            }
            foundBlocks.clear();
        }

        AtomicBoolean foundInTask = new AtomicBoolean(false);

        scanTaskThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted() && !foundInTask.get()
                    && ((timeout <= 0) || (System.currentTimeMillis() - startTime) < timeout)) {
                glassScanTask = new ScanTask(pos -> {
                    foundInTask.set(true);
                    setFoundBlock(pos);
                    double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2)
                            + Math.pow(client.player.getY() - pos.getY(), 2)
                            + Math.pow(client.player.getZ() - pos.getZ(), 2));
                    double roundedDistance = Math.round(distance * 10.0) / 10.0;
                    ChatUtils.sendMessage("§aFound Magenta Stained Glass at §rx: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ()
                            + "\n§dDistance: §r" + roundedDistance);
                    active = false;
                    onFound.run();
                    endTasks();
                }, pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS));

                paneScanTask = new ScanTask(pos -> {
                    foundInTask.set(true);
                    setFoundBlock(pos);
                    double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2)
                            + Math.pow(client.player.getY() - pos.getY(), 2)
                            + Math.pow(client.player.getZ() - pos.getZ(), 2));
                    double roundedDistance = Math.round(distance * 10.0) / 10.0;
                    ChatUtils.sendMessage("§aFound Magenta Stained Glass Pane at §rx: " + pos.getX() + " y: " + pos.getY() + " z: " + pos.getZ()
                            + "\n§dDistance: §r" + roundedDistance);
                    active = false;
                    onFound.run();
                    endTasks();
                }, pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS_PANE));

                glassScanTask.start();
                paneScanTask.start();

                while (glassScanTask != null && paneScanTask != null && !glassScanTask.isDone() && !paneScanTask.isDone() && !foundInTask.get() && !Thread.currentThread().isInterrupted()) {
                	try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                if (glassScanTask != null) {
        	    	glassScanTask.cancel();
        	    	glassScanTask = null;
        		}
        		if (paneScanTask != null) {
        			paneScanTask.cancel();
        	    	paneScanTask = null;
        		}
            }

            if (!foundInTask.get()) {
                active = false;
                onTimeout.run();
            }
        }, "ScanTaskThread");

        scanTaskThread.start();
    }


    private static void endTasks() {
		if (glassScanTask != null) {
	    	glassScanTask.cancel();
	    	glassScanTask = null;
		}
		if (paneScanTask != null) {
			paneScanTask.cancel();
	    	paneScanTask = null;
		}
		if (scanTaskThread != null) {
			scanTaskThread.interrupt();
			scanTaskThread = null;
		}
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

    private static synchronized void setFoundBlock(BlockPos pos) {
        found = true;
        foundBlocks.add(pos);
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
        Render.addBlock(pos, new ArrayList<>(List.of(r, g, b, a)), (boolean) ConfigManager.getConfig("fullHighlight") ? RenderMode.HIGHLIGHT : RenderMode.OUTLINE);
    }
}