package com.sbc.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class ScoreboardUtils {
    public static final List<String> STRING_SCOREBOARD = Collections.synchronizedList(new ArrayList<>());
    public static final List<Text> TEXT_SCOREBOARD = Collections.synchronizedList(new ArrayList<>());

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(ScoreboardUtils::update);
    }

    public static String getString(){
        StringBuilder out = new StringBuilder();
        synchronized (STRING_SCOREBOARD) {
            for (String line : STRING_SCOREBOARD){
                out.append(line);
            }
        }
        return out.toString();
    }

    public static boolean contains(String text){
        return getString().contains(text);
    }

    private static void update(MinecraftClient client) {
        try {
            synchronized (STRING_SCOREBOARD) {
                STRING_SCOREBOARD.clear();
            }
            synchronized (TEXT_SCOREBOARD) {
                TEXT_SCOREBOARD.clear();
            }

            ClientPlayerEntity player = client.player;
            if (player == null) return;

            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            ArrayList<Text> textLines = new ArrayList<>();
            ArrayList<String> stringLines = new ArrayList<>();

            for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
                //Limit to just objectives displayed in the scoreboard (specifically sidebar objective)
                if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
                    Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());

                    if (team != null) {
                        Text textLine = Text.empty().append(team.getPrefix().copy()).append(team.getSuffix().copy());
                        String strLine = team.getPrefix().getString() + team.getSuffix().getString();

                        if (!strLine.trim().isEmpty()) {
                            String formatted = Formatting.strip(strLine);

                            textLines.add(textLine);
                            stringLines.add(formatted);
                        }
                    }
                }
            }

            if (objective != null) {
                stringLines.add(objective.getDisplayName().getString());
                textLines.add(Text.empty().append(objective.getDisplayName().copy()));

                Collections.reverse(stringLines);
                Collections.reverse(textLines);
            }

            synchronized (STRING_SCOREBOARD) {
                STRING_SCOREBOARD.addAll(stringLines);
            }
            synchronized (TEXT_SCOREBOARD) {
                TEXT_SCOREBOARD.addAll(textLines);
            }
        } catch (NullPointerException e) {
            // Do nothing
        }
    }


}