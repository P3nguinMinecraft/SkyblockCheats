package com.sbc.command;

import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sbc.util.ChatUtils;
import com.sbc.util.Config;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ConfigReset {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
    	var command = ClientCommandManager.literal("config")
            .then(ClientCommandManager.literal("reset")
                .then(ClientCommandManager.argument("key", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (String key : Config.getAllKeys()) {
                            builder.suggest(key);
                        }
                        builder.suggest("all");
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        String key = StringArgumentType.getString(ctx, "key");
                        if (key.equals("all")) {
                        	UUID id = UUID.randomUUID();
                            Action.add(id, () -> Config.resetAllConfigs());
                            ChatUtils.sendFormattedMessage(
            				    Text.literal("§4[!] Click to reset all configs [!]").setStyle(Style.EMPTY
            				        .withClickEvent(new ClickEvent.RunCommand("/sbc action " + id))
            				        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eCLICK")))
        				    ));
                        }
                        else {
	                        Config.resetConfig(key);
	                        ChatUtils.sendMessage("Removed config key: " + key);
                        }
                        return 1;
                    })
                )
            );
    	
        dispatcher.register(ClientCommandManager.literal("sbc").then(command));
        dispatcher.register(ClientCommandManager.literal("skyblockcheats").then(command));
    }
}
