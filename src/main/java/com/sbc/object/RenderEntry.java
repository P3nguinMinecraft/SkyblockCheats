package com.sbc.object;

import java.util.ArrayList;
import java.util.UUID;

public class RenderEntry {
	public final ArrayList<Coordinate> coords;
    public final Color color;
    public final RenderMode mode;
    
    public UUID id;
    public float lineWidth;

    public RenderEntry(ArrayList<Coordinate> coords, Color color, RenderMode mode) {
    	this.coords = coords;
        this.color = color;
        this.mode = mode;
    }
    
    public RenderEntry setId(UUID id) {
		this.id = id;
		return this;
	}
    
    public RenderEntry setLineWidth(float lineWidth) {
    	if (mode == RenderMode.LINES || mode == RenderMode.LINES_THROUGH_WALLS) {
			this.lineWidth = lineWidth;
		}
    	else {
			throw new IllegalArgumentException("Line width can only be set for LINES or LINES_THROUGH_WALLS modes.");
		}
    	return this;
    }
    
    @Override
    public String toString() {
		return "RenderEntry: { " +
				"coords = " + coords.toString() +
				", color = " + color.toString() +
				", mode = " + mode +
				", UUID = " + id.toString() +
				" }";
	}
    
    public enum RenderMode {
        FILLED,
        FILLED_THROUGH_WALLS,
        LINES,
        LINES_THROUGH_WALLS,
    }
}

