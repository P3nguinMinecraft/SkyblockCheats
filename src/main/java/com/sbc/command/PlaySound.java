package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.SoundUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PlaySound {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("sbc")
            .then(ClientCommandManager.literal("playsound")
                .then(ClientCommandManager.argument("sound", StringArgumentType.word())
                    .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0.0f))
                        .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(0.0f))
                            .executes(ctx -> {
                                String soundId = StringArgumentType.getString(ctx, "sound");
                                float volume = FloatArgumentType.getFloat(ctx, "volume");
                                float pitch = FloatArgumentType.getFloat(ctx, "pitch");
                                
                    			if (!soundId.startsWith("minecraft:")) {
                    				soundId = "minecraft:" + soundId;
                    			}
                    			
                                Identifier id = new Identifier(soundId);
                                SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
                                
                                if (soundEvent == null) {
                                    ChatUtils.sendMessage("§cSound not found: " + soundId);
                                    return 0;
                                }

                                SoundUtils.playSound(volume, pitch, soundEvent);

                                return 1;
                            })
                        )
                    )
                )
            )
        );
        dispatcher.register(ClientCommandManager.literal("sbc")
            .then(ClientCommandManager.literal("playsound")
                .then(ClientCommandManager.argument("sound", StringArgumentType.word())
                    .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0.0f))
                        .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(0.0f))
                            .executes(ctx -> {
                                String soundId = StringArgumentType.getString(ctx, "sound");
                                float volume = FloatArgumentType.getFloat(ctx, "volume");
                                float pitch = FloatArgumentType.getFloat(ctx, "pitch");
                                
                    			if (!soundId.startsWith("minecraft:")) {
                    				soundId = "minecraft:" + soundId;
                    			}
                    			
                                Identifier id = new Identifier(soundId);
                                SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
                                
                                if (soundEvent == null) {
                                    ChatUtils.sendMessage("§cSound not found: " + soundId);
                                    return 0;
                                }

                                SoundUtils.playSound(volume, pitch, soundEvent);

                                return 1;
                            })
                        )
                    )
                )
            )
        );
    }
}