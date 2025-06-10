package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.SoundUtils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PlaySound {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.literal("playsound")
		    .then(ClientCommandManager.argument("sound", IdentifierArgumentType.identifier())
		        .suggests((ctx, builder) -> {
		            for (Identifier id : Registries.SOUND_EVENT.getIds()) {
		                builder.suggest(id.toString());
		            }
		            return builder.buildFuture();
		        })
		        .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0.0f))
		            .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(0.0f))
		                .executes(ctx -> {
		                    Identifier id = ctx.getArgument("sound", Identifier.class);
		                    float volume = FloatArgumentType.getFloat(ctx, "volume");
		                    float pitch = FloatArgumentType.getFloat(ctx, "pitch");

		                    SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);

		                    if (soundEvent == null) {
		                        ChatUtils.sendMessage("§cSound not found: " + id);
		                        return 0;
		                    }

		                    SoundUtils.playSound(volume, pitch, soundEvent);
		                    return 1;
		                })
		            )
		        )
		    );
        
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}