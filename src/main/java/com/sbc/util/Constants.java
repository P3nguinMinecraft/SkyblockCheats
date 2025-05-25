package com.sbc.util;

import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;

import com.sbc.object.Coordinate;

public class Constants {
	public static final Predicate<BlockPos> MAGENTA_GLASS_RECTANGLE_EXCLUSION = pos -> {
		final Coordinate pos1 = new Coordinate(501, 105, 554);
		final Coordinate pos2 = new Coordinate(524, 121, 561);
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        boolean isInRegion = x >= pos1.x && x <= pos2.x &&
                             y >= pos1.y && y <= pos2.y &&
                             z >= pos1.z && z <= pos2.z;
        return isInRegion;
    };
}
