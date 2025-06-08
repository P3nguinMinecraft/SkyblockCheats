package com.sbc.object;

import com.sbc.util.ChatUtils;

public class Color {
    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public Color(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    public Color(int r, int g, int b, int a) {
        this(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }
    
    public static Color parseString(String color) {
    	String[] parts = color.split("\\.");
		if (parts.length < 3 || parts.length > 4) {
			ChatUtils.sendMessage("§cInvalid rgbaBlockColor format. Expected format: r.g.b.a Got " + parts.toString());
			return new Color(1.0f, 1.0f, 1.0f, 1.0f);
		}
		Float r = Float.parseFloat(parts[0].trim());
		Float g = Float.parseFloat(parts[1].trim());
		Float b = Float.parseFloat(parts[2].trim());
		Float a = parts.length == 4 ? Float.parseFloat(parts[3].trim()) : 1.0f;
		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 1) {
			ChatUtils.sendMessage("§cInvalid rgbaBlockColor values. Expected values: r(0-255).g(0-255).b(0-255).a(0-1) Got " + r + "." + g + "." + b + "." + a);
			return new Color(1.0f, 1.0f, 1.0f, 1.0f);
		}
        return new Color(
        	(float) Integer.parseInt(parts[0])/255f,
        	(float) Integer.parseInt(parts[1])/255f, 
        	(float) Integer.parseInt(parts[2])/255f,
			parts.length == 4 ? Float.parseFloat(parts[3]) : 1.0f
		);

    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    private float clamp(float val) {
        return Math.max(0f, Math.min(1f, val));
    }
}
