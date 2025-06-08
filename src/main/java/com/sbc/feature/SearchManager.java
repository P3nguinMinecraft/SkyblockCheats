package com.sbc.feature;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sbc.object.Color;
import com.sbc.task.BlockScanTask;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;
import com.sbc.util.Render;
import com.sbc.util.Render.RenderMode;
import com.sbc.util.SoundUtils;
import com.sbc.util.World;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SearchManager {
    public static ArrayList<BlockPos> foundBlocks = new ArrayList<>();
    public static volatile boolean loopActive = false;
    public static volatile boolean active = false;
    private static volatile boolean found = false;
    private static Thread searchLoopThread = null;
    private static Thread manualScanTaskThread = null;
    private static Thread scanTaskThread = null;
    private static BlockScanTask glassScanTask, paneScanTask;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static synchronized void toggleSearch() {
        if (loopActive) {
            clearSearch();
            endTasks();
            ChatUtils.addMessage("§2[SBC] §r§cSearch stopped");
        }
        else {
           	if (found) {
                found = false;
            	ChatUtils.addMessage("§2[SBC] §r§eSearch cleared");
            	for (BlockPos pos : foundBlocks) {
					Render.removeBlock(pos);
				}
                foundBlocks.clear();
           	}
            ChatUtils.addMessage("§2[SBC] §r§aSearch started");
            runSearchLoop();
        }
    }

    public static synchronized void clearSearch() {
        if (found) {
            found = false;
        	ChatUtils.addMessage("§2[SBC] §r§eSearch cleared");
        	for (BlockPos pos : foundBlocks) {
				Render.removeBlock(pos);
			}
            foundBlocks.clear();
       	}
    }

    public static synchronized void scan() {
    	if (active) {
			active = false;
			ChatUtils.addMessage("§2[SBC] §r§cScan cancelled");
		    endTasks();
		    return;
		}
    	ChatUtils.addMessage("§2[SBC] §r§aScan started");

		manualScanTaskThread = new Thread(() -> {
	    	Object lock = new Object();
			runScanTaskAsync(0,
			    () -> {
			    	synchronized (lock) { lock.notify(); }
			    },
			    () -> {
			    	synchronized (lock) { lock.notify(); }
			    }, false
			);

            synchronized (lock) {
            	try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
            }
	    }, "ManualScanTaskThread");
		manualScanTaskThread.start();
    }

    public static void listSearch() {
    	if (foundBlocks.isEmpty()) {
			ChatUtils.addMessage("§2[SBC] §r§cNo blocks found");
		}
    	else {
			for (BlockPos pos : foundBlocks) {
				String block = pos.getX() + " " + pos.getY() + " " + pos.getZ();

				ChatUtils.sendFormattedMessage(
				    Text.literal("§b" + block).setStyle(Style.EMPTY
				        .withClickEvent(new ClickEvent.CopyToClipboard(block))
				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto copy")))
				    ),
				    Text.literal(" §r[Look]").setStyle(Style.EMPTY
				        .withClickEvent(new ClickEvent.RunCommand("/sbc look block " + block))
				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto look at block")))
				    )
				);
			}
		}
    }

    private static void runSearchLoop() {
    	loopActive = true;
        searchLoopThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && loopActive && !found) {
                    ChatUtils.sendServerMessage("/warp " + (String) Config.getConfig("warpOut"));
                    
                	if ((int) Config.getConfig("delay") > 0) {
                		sleepInterruptibly((int) Config.getConfig("delay") * 1000);
                	}
                	else {
                		ChatUtils.waitForChatMessage("Profile ID:", false, true, 5000, () -> {}, () -> {});
                	}
                	
                	if (!loopActive) break;

                	ChatUtils.sendServerMessage("/warp " + (String) Config.getConfig("warpIn"));

                    Object lock = new Object();
                    AtomicBoolean foundTask = new AtomicBoolean(false);

                	AtomicBoolean chProfile = new AtomicBoolean(false);
                	ChatUtils.waitForChatMessage("Profile ID:", false, false, 10000, 
            			() -> {
	                		if (chProfile != null)
	                			chProfile.set(true);
            			},
						() -> {
							if (chProfile != null)
	                			chProfile.set(true);
						}
					);
                	
                	World.waitLoaded(3000);

                    runScanTaskAsync(10000,
                        () -> {
                        	loopActive = false;
                            foundTask.set(true);
                            if ((boolean) Config.getConfig("pingOnFound")) {
                        		String soundId = (String) Config.getConfig("pingSound");
                                float volume = (Float) Config.getConfig("pingVolume");
                                float pitch = (Float) Config.getConfig("pingPitch");

                                Identifier id = Identifier.tryParse(soundId);
                                SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);

                                SoundUtils.playSound((float) volume, (float) pitch, soundEvent);
                        	}
                            synchronized (lock) { lock.notify(); }
                        },
                        () -> {
                            synchronized (lock) { lock.notify(); }
                        }, 
                        true
                    );

                    synchronized (lock) {
                        lock.wait();
                    }

        			if (!loopActive) break;
        			
                    if (!foundTask.get()) {
                    	if ((int) Config.getConfig("delay") > 0) {
                    		sleepInterruptibly((int) Config.getConfig("delay") * 1000);
                    	}
                    	else {
                    		while (!chProfile.get()) {
                    			try {
									sleepInterruptibly(10);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								}
                    		}
                    	}
					}

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SearchLoopThread");

        searchLoopThread.start();
    }

    private static void runScanTaskAsync(int timeout, Runnable onFound, Runnable onTimeout, boolean single) {
        if (active) {
            ChatUtils.addMessage("§2[SBC] §r§cA scan is already in progress!");
            return;
        }
        active = true;

        clearSearch();

        AtomicBoolean foundInTask = new AtomicBoolean(false);

        scanTaskThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            boolean firstRun = true;
            
            while (!Thread.currentThread().isInterrupted() && !foundInTask.get()
                    && ((timeout <= 0) || (System.currentTimeMillis() < startTime + timeout)) && active) {
                glassScanTask = new BlockScanTask(
            		pos -> {
		                foundInTask.set(true);
		                setFoundBlock(pos);
		                double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2)
	                            + Math.pow(client.player.getY() - pos.getY(), 2)
	                            + Math.pow(client.player.getZ() - pos.getZ(), 2));
	                    double roundedDistance = Math.round(distance * 10.0) / 10.0;
	                    ChatUtils.addMessage("§2[SBC] §r§aFound Magenta Stained Glass §r" + roundedDistance + " blocks away");
	                    String block = pos.getX() + " " + pos.getY() + " " + pos.getZ();
	                    ChatUtils.sendFormattedMessage(
        				    Text.literal("§b" + block).setStyle(Style.EMPTY
        				        .withClickEvent(new ClickEvent.CopyToClipboard(block))
        				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto copy")))
        				    ),
        				    Text.literal(" §r[Look]").setStyle(Style.EMPTY
        				        .withClickEvent(new ClickEvent.RunCommand("/sbc look block " + block))
        				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto look at block")))
        				    )
        				);
		                active = false;
		                onFound.run();
		                endScanTasks();
            		},
            		pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS)
            	);

                paneScanTask = new BlockScanTask(
            		pos -> {
	                    foundInTask.set(true);
	                    setFoundBlock(pos);
	                    double distance = Math.sqrt(Math.pow(client.player.getX() - pos.getX(), 2)
	                            + Math.pow(client.player.getY() - pos.getY(), 2)
	                            + Math.pow(client.player.getZ() - pos.getZ(), 2));
	                    double roundedDistance = Math.round(distance * 10.0) / 10.0;
	                    ChatUtils.addMessage("§2[SBC] §r§aFound Magenta Stained Glass Pane §r" + roundedDistance + " blocks away");
	                    String block = pos.getX() + " " + pos.getY() + " " + pos.getZ();
	                    ChatUtils.sendFormattedMessage(
        				    Text.literal("§b" + block).setStyle(Style.EMPTY
        				        .withClickEvent(new ClickEvent.CopyToClipboard(block))
        				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto copy")))
        				    ),
        				    Text.literal(" §r[Look]").setStyle(Style.EMPTY
        				        .withClickEvent(new ClickEvent.RunCommand("/sbc look block " + block))
        				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK §rto look at block")))
        				    )
        				);
	                    active = false;
	                    onFound.run();
	                    endScanTasks();
	                },
            		pos -> client.world.getBlockState(pos).isOf(Blocks.MAGENTA_STAINED_GLASS_PANE)
                );

                glassScanTask.start();
                paneScanTask.start();

                while (active && glassScanTask != null && paneScanTask != null && (!glassScanTask.isDone() || !paneScanTask.isDone()) && !foundInTask.get() && !Thread.currentThread().isInterrupted()) {
                	try {
                		if ((timeout > 0) && System.currentTimeMillis() >= startTime + timeout) {
                			active = false;
                			break;
                		}
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
        		
        		if (firstRun && !foundInTask.get()) {
        			firstRun = false;
    				ChatUtils.addMessage("§2[SBC] §r" + (single ? "§cNo blocks found" : "§cNo blocks found. §r§eLooping..."));
        			if (single) {
						active = false;
						break;
        			}
        		}
            }

            if (!foundInTask.get()) {
                onTimeout.run();
            }

        	active = false;
			endScanTasks();
        }, "ScanTaskThread");

        scanTaskThread.start();
    }


    public static void endTasks() {
    	loopActive = false;
    	active = false;
		if (searchLoopThread != null) {
			searchLoopThread.interrupt();
			searchLoopThread = null;
		}
		endScanTasks();
	}
    
    public static void endScanTasks() {
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
		if (manualScanTaskThread != null) {
			manualScanTaskThread.interrupt();
			manualScanTaskThread = null;
		}
    }

    private static void sleepInterruptibly(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    private static synchronized void setFoundBlock(BlockPos pos) {
        found = true;
        foundBlocks.add(pos);
        String rgba = (String) Config.getConfig("rgbaBlockColor");
        Render.addBlock(pos, Color.parseString(rgba), (boolean) Config.getConfig("fullHighlight") ? RenderMode.HIGHLIGHT : RenderMode.OUTLINE);
    }
}