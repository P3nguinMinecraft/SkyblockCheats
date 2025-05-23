package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.SoundUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PlaySound {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
            .then(ClientCommandManager.literal("playsound")
                .then(ClientCommandManager.argument("sound", StringArgumentType.word())
                    .then(ClientCommandManager.argument("volume", DoubleArgumentType.doubleArg(0.0))
                        .then(ClientCommandManager.argument("pitch", DoubleArgumentType.doubleArg(0.0))
                            .executes(ctx -> {
                                String soundId = StringArgumentType.getString(ctx, "sound");
                                double volume = DoubleArgumentType.getDouble(ctx, "volume");
                                double pitch = DoubleArgumentType.getDouble(ctx, "pitch");
                                
                    			if (!soundId.startsWith("minecraft:")) {
                    				soundId = "minecraft:" + soundId;
                    			}
                    			
                                Identifier id = new Identifier(soundId);
                                SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
                                
                                if (soundEvent == null) {
                                    ChatUtils.sendMessage("§cSound not found: " + soundId);
                                    return 0;
                                }

                                SoundUtils.playSound((float) volume, (float) pitch, soundEvent);

                                return 1;
                            })
                        )
                    )
                )
            )
        );
        dispatcher.register(ClientCommandManager.literal("skyblockcheats")
			.then(ClientCommandManager.literal("playsound")
				.then(ClientCommandManager.argument("sound", StringArgumentType.word())
					.then(ClientCommandManager.argument("volume", DoubleArgumentType.doubleArg(0.0))
						.then(ClientCommandManager.argument("pitch", DoubleArgumentType.doubleArg(0.0))
							.executes(ctx -> {
								String soundId = StringArgumentType.getString(ctx, "sound");
								double volume = DoubleArgumentType.getDouble(ctx, "volume");
								double pitch = DoubleArgumentType.getDouble(ctx, "pitch");

								MinecraftClient client = MinecraftClient.getInstance();
								Identifier id = new Identifier(soundId);
								SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
								
								if (soundEvent == null) {
									ChatUtils.sendMessage("§cSound not found: " + soundId);
									return 0;
								}

								if (client.player != null && client.world != null) {
									client.getSoundManager().play(
										PositionedSoundInstance.master(
											soundEvent, 
											(float) volume, 
											(float) pitch
										)
									);
									ChatUtils.sendMessage("§aPlaying sound: " + soundId + " §7(volume=" + volume + ", pitch=" + pitch + ")");
								} else {
									ChatUtils.sendMessage("§cFailed to play sound: client not ready");
								}

								return 1;
							})
						)
					)
				)
			)
		);
    }
}