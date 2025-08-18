package com.sbc.object;

import net.minecraft.util.math.Vec3d;

public class BallBounds {
    private Vec3d center;
    private double negX;
    private double posX;
    private double negZ;
    private double posZ;

    public BallBounds(Vec3d center){
        this.center = center;
        this.negX = Double.NEGATIVE_INFINITY;
        this.posX = Double.POSITIVE_INFINITY;
        this.negZ = Double.NEGATIVE_INFINITY;
        this.posZ = Double.POSITIVE_INFINITY;
    }

    public BallBounds(Vec3d center, double negX, double posX, double negZ, double posZ){
        this.center = center;
        this.negX = negX;
        this.posX = posX;
        this.negZ = negZ;
        this.posZ = posZ;
    }

    public Vec3d getCenter(){
        return center;
    }

    public boolean inBounds(double x, double y) {
        return x >= negX && x <= posX && y >= negZ && y <= posZ;
    }
    
    public boolean inBounds(Vec3d coords){
        return inBounds(coords.x, coords.z);
    }
}
