package com.sbc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.HashMap;

public class Render {
    public static HashMap<String, Boolean> toggles = new HashMap<>();
    public static HashMap<String, Class> classes = new HashMap<>();

    public static void init(){
        insert("BlockDisplay", net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity.class);
        insert("ArmorStand", net.minecraft.entity.decoration.ArmorStandEntity.class);
        insert("Player", net.minecraft.entity.player.PlayerEntity.class);
        insert("FallingBlock", net.minecraft.entity.FallingBlockEntity.class);
    }

    private static void insert(String name, Class entity){
        toggles.put(name, true);
        classes.put(name, entity);
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var command = ClientCommandManager.literal("render")
                .then(ClientCommandManager.argument("entity", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (String key : toggles.keySet()) {
                            builder.suggest(key);
                        }
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        String key = ctx.getArgument("entity", String.class);
                        if (toggles.containsKey(key)){
                            toggles.replace(key, !toggles.get(key));
                            ChatUtils.addMessage(key + " rendering is now " + (toggles.get(key) ? "enabled" : "disabled"));
                        }
                        else {
                            ChatUtils.addMessage(key + " is not a valid entity type.");
                        }
                        return 1;
                    })
                );

        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}
