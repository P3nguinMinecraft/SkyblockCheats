package com.sbc.object;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.util.math.BlockPos;

public class Coordinate {
	public float x;
	public float y;
	public float z;
	
	public Coordinate(int x, int y, int z) {
	    this.x = (float) x;
	    this.y = (float) y;
	    this.z = (float) z;
	}

	public Coordinate(float x, float y, float z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public float getX() {
	    return x;
	}

	public float getY() {
	    return y;
	}

	public float getZ() {
	    return z;
	}

	public ArrayList<Float> toArray() {
	    return new ArrayList<>(Arrays.asList(x, y, z));
	}

	public Coordinate setX(float x) {
	    this.x = x;
	    return this;
	}

	public Coordinate setY(float y) {
	    this.y = y;
	    return this;
	}

	public Coordinate setZ(float z) {
	    this.z = z;
	    return this;
	}

	public Coordinate setCoordinate(float x, float y, float z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    return this;
	}

	public Coordinate move(float x, float y, float z) {
	    this.x += x;
	    this.y += y;
	    this.z += z;
	    return this;
	}

	public Coordinate shift(float dx, float dy, float dz) {
	    return new Coordinate(this.x + dx, this.y + dy, this.z + dz);
	}

	public BlockPos toBlockPos() {
	    return new BlockPos((int) x, (int) y, (int) z);
	}

	public static Coordinate convertBlockPos(BlockPos blockPos) {
	    return new Coordinate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
}
