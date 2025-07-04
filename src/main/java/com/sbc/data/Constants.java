package com.sbc.data;

import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;

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
}
