package com.sbc.util;

public class Skyblock {
    public static boolean inCH(){
        return ScoreboardUtils.contains("Jungle")
                || ScoreboardUtils.contains("Mithril Deposits")
                || ScoreboardUtils.contains("Precursor Remnants")
                || ScoreboardUtils.contains("Goblin Holdout")
                || ScoreboardUtils.contains("Crystal Nucleus");
    }
}
