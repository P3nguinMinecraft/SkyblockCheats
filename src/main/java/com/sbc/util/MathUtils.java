package com.sbc.util;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public class MathUtils {
    public static Vec3d averageVec3d(List<Vec3d> vectors) {
        if (vectors.isEmpty()) return Vec3d.ZERO;

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3d vec : vectors) {
            sumX += vec.x;
            sumY += vec.y;
            sumZ += vec.z;
        }

        int size = vectors.size();
        return new Vec3d(sumX / size, sumY / size, sumZ / size);
    }

    public static double averageDouble(List<Double> doubles){
        double sum = 0;
        for (double value : doubles){
            sum += value;
        }
        return sum / doubles.size();
    }
}
