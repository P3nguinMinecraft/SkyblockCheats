package com.sbc.data;

import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.util.math.Vec3d;

public class Constants {
	public static final Predicate<BlockPos> MAGENTA_GLASS_RECTANGLE_EXCLUSION = pos -> {
		final Vec3d pos1 = new Vec3d(501, 105, 554);
		final Vec3d pos2 = new Vec3d(524, 121, 561);
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        boolean isInRegion = x >= pos1.x && x <= pos2.x &&
                             y >= pos1.y && y <= pos2.y &&
                             z >= pos1.z && z <= pos2.z;
        return isInRegion;
    };

    public static final Vec3d DUNGEON_HUB_BALL = new Vec3d(-110, 102, 0);

    public static final Pattern IMPEL_REGEX = Pattern.compile("Impel:\\s+(?:§.\\s*)*([A-Z]+(?:\\s+[A-Z]+)*)\\s+(?:§.\\s*)*(\\d+(?:\\.\\d+)?)s");

    public static final String CONFIG_FILE_PATH = "./config/skyblockcheats/config.toml";

    public static final Set<String> CRYSTAL_HOLLOWS_LOCATIONS = Set.of(
            "Jungle",
            "Jungle Temple",
            "Mithril Deposits",
            "Mines of Divan",
            "Dragon's Lair",
            "Precursor Remnants",
            "Lost Precursor City",
            "Goblin Holdout",
            "Goblin Queen's Den",
            "Crystal Nucleus",
            "Magma Fields",
            "Khazad-dûm",
            "Fairy Grotto"
    );
}
