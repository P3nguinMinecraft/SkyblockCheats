package com.sbc.util;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.sbc.object.Color;
import com.sbc.object.Coordinate;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Render {
	
    public static HashMap<BlockPos, RenderEntry> renderQueue;

    public static void init() {
        renderQueue = new HashMap<>();
        WorldRenderEvents.AFTER_ENTITIES.register(Render::render);
    }

    public static void addBlock(BlockPos block, Color color, RenderMode mode) {
        renderQueue.put(block, new RenderEntry(color, mode));
    }

    public static void removeBlock(BlockPos block) {
        renderQueue.remove(block);
    }

    public static synchronized void render(WorldRenderContext context) {
        if (renderQueue.isEmpty()) return;

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();

        MatrixStack ms = context.matrixStack();
        ms.push();
        ms.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        GlStateManager._disableDepthTest();
		//GlStateManager._depthFunc(GlConst.GL_ALWAYS);
		// [!] Depth test is not working for VertexConsumer
        
		boolean hasBuffer = false;
		
        BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        VertexConsumerProvider consumers = context.consumers();
		VertexConsumer vc = consumers.getBuffer(RenderLayer.getLines());

        for (Map.Entry<BlockPos, RenderEntry> entry : renderQueue.entrySet()) {
            BlockPos block = entry.getKey();
            RenderEntry renderEntry = entry.getValue();
            if (renderEntry.color.getA() <= 0f) continue;

            if (renderEntry.mode == RenderMode.HIGHLIGHT) {
                renderBlockFaces(buffer, ms, block, renderEntry.color);
                if (!hasBuffer) hasBuffer = true;
            } else {
                renderBlockOutline(vc, ms, block, renderEntry.color);
            }
        }
        
        // [!] How to render BufferBuilder
        
        if (hasBuffer) {
            //Tessellator.getInstance();
        	buffer.end().close();
        }
		
		//GlStateManager._depthFunc(GlConst.GL_LEQUAL);
        
        ms.pop();
    }
    
    // Block outline rendering

    private static void renderBlockOutline(VertexConsumer vc, MatrixStack ms, BlockPos block, Color color) {
        Coordinate coord = Coordinate.convertBlockPos(block);
        renderFaceOutline(vc, ms, color, coord.shift(0f, 0.5f, 0.5f), 0);
        renderFaceOutline(vc, ms, color, coord.shift(1f, 0.5f, 0.5f), 0);
        renderFaceOutline(vc, ms, color, coord.shift(0.5f, 0f, 0.5f), 1);
        renderFaceOutline(vc, ms, color, coord.shift(0.5f, 1f, 0.5f), 1);
        renderFaceOutline(vc, ms, color, coord.shift(0.5f, 0.5f, 0f), 2);
        renderFaceOutline(vc, ms, color, coord.shift(0.5f, 0.5f, 1f), 2);
    }
    
    private static void renderFaceOutline(VertexConsumer vc, MatrixStack ms, Color color, Coordinate coord, int direction) {
        float d = 0.5f; // distance from center

        switch (direction) {
            case 0 -> {
                drawEdge(vc, ms, color, coord.shift(0, -d, -d), coord.shift(0, -d, d));
                drawEdge(vc, ms, color, coord.shift(0, -d, d), coord.shift(0, d, d));
                drawEdge(vc, ms, color, coord.shift(0, d, d), coord.shift(0, d, -d));
                drawEdge(vc, ms, color, coord.shift(0, d, -d), coord.shift(0, -d, -d));
            }
            case 1 -> {
                drawEdge(vc, ms, color, coord.shift(-d, 0, -d), coord.shift(-d, 0, d));
                drawEdge(vc, ms, color, coord.shift(-d, 0, d), coord.shift(d, 0, d));
                drawEdge(vc, ms, color, coord.shift(d, 0, d), coord.shift(d, 0, -d));
                drawEdge(vc, ms, color, coord.shift(d, 0, -d), coord.shift(-d, 0, -d));
            }
            case 2 -> {
                drawEdge(vc, ms, color, coord.shift(-d, -d, 0), coord.shift(-d, d, 0));
                drawEdge(vc, ms, color, coord.shift(-d, d, 0), coord.shift(d, d, 0));
                drawEdge(vc, ms, color, coord.shift(d, d, 0), coord.shift(d, -d, 0));
                drawEdge(vc, ms, color, coord.shift(d, -d, 0), coord.shift(-d, -d, 0));
            }
        }
    }

    private static void drawEdge(VertexConsumer vc, MatrixStack ms, Color color, Coordinate from, Coordinate to) {
        MatrixStack.Entry entry = ms.peek();
        Matrix4f pm = entry.getPositionMatrix();
        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float dz = to.z - from.z;
        float length = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);

        float nx = (length != 0f) ? dx / length : 0f;
        float ny = (length != 0f) ? dy / length : 0f;
        float nz = (length != 0f) ? dz / length : 0f;

        vc.vertex(pm, from.x, from.y, from.z).color(r, g, b, a).normal(nx, ny, nz);
        vc.vertex(pm, to.x, to.y, to.z).color(r, g, b, a).normal(nx, ny, nz);
    }
    
    // Block highlight rendering
    
    private static void renderBlockFaces(BufferBuilder buffer, MatrixStack ms, BlockPos block, Color color) {
        MatrixStack.Entry entry = ms.peek();
        Matrix4f pm = entry.getPositionMatrix();

        float x = block.getX(), y = block.getY(), z = block.getZ();
        float size = 1.0f;
        float os = 0.001f;

        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        // Front
        buffer.vertex(pm, x - os, y - os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y - os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y + size + os, z + size + os).color(r, g, b, a);

        // Back
        buffer.vertex(pm, x + size + os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y + size + os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z - os).color(r, g, b, a);

        // Left
        buffer.vertex(pm, x - os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y - os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y + size + os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y + size + os, z - os).color(r, g, b, a);

        // Right
        buffer.vertex(pm, x + size + os, y - os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z + size + os).color(r, g, b, a);

        // Bottom
        buffer.vertex(pm, x - os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y - os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y - os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y - os, z + size + os).color(r, g, b, a);

        // Top
        buffer.vertex(pm, x - os, y + size + os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z + size + os).color(r, g, b, a);
        buffer.vertex(pm, x + size + os, y + size + os, z - os).color(r, g, b, a);
        buffer.vertex(pm, x - os, y + size + os, z - os).color(r, g, b, a);
    }


    private static void renderBlockFaces1(VertexConsumer vc, MatrixStack ms, BlockPos block, Color color) {
        MatrixStack.Entry entry = ms.peek();
        Matrix4f pm = entry.getPositionMatrix();

        float x = block.getX(), y = block.getY(), z = block.getZ();
        float size = 1.0f;
        float os = 0.001f; // offset for z-fighting

        float r = color.getR(), g = color.getG(), b = color.getB(), a = color.getA();

        // Front face (normal 0,0,1)
        drawQuadAsTriangles(vc, pm, r, g, b, a, 0f, 0f, 1f,
            x - os, y - os, z + size + os,
            x + size + os, y - os, z + size + os,
            x + size + os, y + size + os, z + size + os,
            x - os, y + size + os, z + size + os);

        // Back face (normal 0,0,-1)
        drawQuadAsTriangles(vc, pm, r, g, b, a, 0f, 0f, -1f,
            x + size + os, y - os, z - os,
            x - os, y - os, z - os,
            x - os, y + size + os, z - os,
            x + size + os, y + size + os, z - os);

        // Left face (normal -1,0,0)
        drawQuadAsTriangles(vc, pm, r, g, b, a, -1f, 0f, 0f,
            x - os, y - os, z - os,
            x - os, y - os, z + size + os,
            x - os, y + size + os, z + size + os,
            x - os, y + size + os, z - os);

        // Right face (normal 1,0,0)
        drawQuadAsTriangles(vc, pm, r, g, b, a, 1f, 0f, 0f,
            x + size + os, y - os, z + size + os,
            x + size + os, y - os, z - os,
            x + size + os, y + size + os, z - os,
            x + size + os, y + size + os, z + size + os);

        // Bottom face (normal 0,-1,0)
        drawQuadAsTriangles(vc, pm, r, g, b, a, 0f, -1f, 0f,
            x - os, y - os, z - os,
            x + size + os, y - os, z - os,
            x + size + os, y - os, z + size + os,
            x - os, y - os, z + size + os);

        // Top face (normal 0,1,0)
        drawQuadAsTriangles(vc, pm, r, g, b, a, 0f, 1f, 0f,
            x - os, y + size + os, z + size + os,
            x + size + os, y + size + os, z + size + os,
            x + size + os, y + size + os, z - os,
            x - os, y + size + os, z - os);
    }
    
    private static void drawQuadAsTriangles(VertexConsumer vc, Matrix4f pm,
        float r, float g, float b, float a,
        float nx, float ny, float nz,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float x3, float y3, float z3) {
		// Triangle 1: v0, v1, v2
		vc.vertex(pm, x0, y0, z0).color(r, g, b, a).normal(nx, ny, nz);
		vc.vertex(pm, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz);
		vc.vertex(pm, x2, y2, z2).color(r, g, b, a).normal(nx, ny, nz);
		
		// Triangle 2: v2, v3, v0
		vc.vertex(pm, x2, y2, z2).color(r, g, b, a).normal(nx, ny, nz);
		vc.vertex(pm, x3, y3, z3).color(r, g, b, a).normal(nx, ny, nz);
		vc.vertex(pm, x0, y0, z0).color(r, g, b, a).normal(nx, ny, nz);
	}

    public static class RenderEntry {
        public final Color color;
        public final RenderMode mode;

        public RenderEntry(Color color, RenderMode mode) {
            this.color = color;
            this.mode = mode;
        }
    }

    public enum RenderMode {
        OUTLINE,
        HIGHLIGHT
    }
}
