package com.sbc.object;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.util.math.BlockPos;

public class Coordinate {
	public double x;
	public double y;
	public double z;

	public Coordinate(double x, double y, double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public double getX() {
	    return x;
	}

	public double getY() {
	    return y;
	}

	public double getZ() {
	    return z;
	}

	public ArrayList<Double> toArray() {
	    return new ArrayList<>(Arrays.asList(x, y, z));
	}

	public Coordinate setX(double x) {
	    this.x = x;
	    return this;
	}

	public Coordinate setY(double y) {
	    this.y = y;
	    return this;
	}

	public Coordinate setZ(double z) {
	    this.z = z;
	    return this;
	}

	public Coordinate setCoordinate(double x, double y, double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    return this;
	}

	public Coordinate move(double x, double y, double z) {
	    this.x += x;
	    this.y += y;
	    this.z += z;
	    return this;
	}

	public Coordinate shift(double dx, double dy, double dz) {
	    return new Coordinate(this.x + dx, this.y + dy, this.z + dz);
	}


	public static Coordinate convertBlockPos(BlockPos blockPos) {
	    return new Coordinate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
}
