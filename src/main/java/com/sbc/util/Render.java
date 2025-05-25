package com.sbc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sbc.object.Coordinate;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Render {
    private static VertexBuffer vertexBuffer;
    public static AtomicBoolean requestedRefresh = new AtomicBoolean(false);
    private static int canvasLoaded = -1;

    public static HashMap<BlockPos, RenderEntry> renderQueue;

    public static void init() {
        renderQueue = new HashMap<>();
        WorldRenderEvents.AFTER_ENTITIES.register(Render::render);
    }

    public static void addBlock(BlockPos block, ArrayList<Float> color, RenderMode mode) {
        requestedRefresh.set(true);
        renderQueue.put(block, new RenderEntry(color, mode));
    }

    public static void removeBlock(BlockPos block) {
        requestedRefresh.set(true);
        renderQueue.remove(block);
    }

    public static synchronized void render(WorldRenderContext context) {
        if (renderQueue.isEmpty()) {
            if (vertexBuffer != null) {
                vertexBuffer.close();
                vertexBuffer = null;
            }
            return;
        }

        if (canvasLoaded == -1) {
            canvasLoaded = FabricLoader.getInstance().isModLoaded("canvas") ? 1 : 0;
        }

        if (vertexBuffer == null || requestedRefresh.get()) {
            requestedRefresh.set(false);
            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            for (BlockPos block : renderQueue.keySet()) {
                RenderEntry entry = renderQueue.get(block);
                if (entry.color.get(3) <= 0) {
                	ChatUtils.sendMessage("Alpha value is 0, render will not appear!");
                	continue;
                }
                if (entry.mode == RenderMode.HIGHLIGHT) {
                    renderBlockFaces(buffer, block, entry.color);
                } else {
                    renderBlockOutline(buffer, block, entry.color);
                }
            }

            vertexBuffer.bind();
            vertexBuffer.upload(buffer.end());
            VertexBuffer.unbind();

        }

        if (vertexBuffer != null) {
            Camera camera = context.camera();
            Vec3d cameraPos = camera.getPos();

            MatrixStack matrices = context.matrixStack();
            matrices.push();

            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            if (canvasLoaded == 1) {
                float pitch = camera.getPitch() * 0.017453292F;
                float yaw = (camera.getYaw() + 180f) * 0.017453292F;

                Quaternionf pitchQuat = new Quaternionf().rotateX(pitch);
                Quaternionf yawQuat = new Quaternionf().rotateY(yaw);
                pitchQuat.mul(yawQuat);
                matrices.multiply(pitchQuat);
            }

            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            Matrix4f modelMatrix = matrices.peek().getPositionMatrix();
            Matrix4f projectionMatrix = new Matrix4f(context.projectionMatrix());

            vertexBuffer.bind();
            vertexBuffer.draw(modelMatrix, projectionMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();

            matrices.pop();

            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }
    }

    private static void renderBlockOutline(BufferBuilder buffer, BlockPos block, ArrayList<Float> color) {
    	Coordinate coord = Coordinate.convertBlockPos(block);

        renderFaceOutline(buffer, color, coord.shift(0, 0.5, 0.5), 0);
        renderFaceOutline(buffer, color, coord.shift(1, 0.5, 0.5), 0);
        renderFaceOutline(buffer, color, coord.shift(0.5, 0, 0.5), 1);
        renderFaceOutline(buffer, color, coord.shift(0.5, 1, 0.5), 1);
        renderFaceOutline(buffer, color, coord.shift(0.5, 0.5, 0), 2);
        renderFaceOutline(buffer, color, coord.shift(0.5, 0.5, 1), 2);
    }

    private static void renderFaceOutline(BufferBuilder buffer, ArrayList<Float> color, Coordinate coord, int direction) {
		final float thickness = (float) Config.getConfig("outlineWeight");
		ArrayList<Coordinate> coords = new ArrayList<>();
		ArrayList<Integer> directions = new ArrayList<>();

		float dist = 0.5f - thickness / 2f;

		if (direction == 0) {
	        coords.add(coord.shift(0, -dist, -dist));
	        directions.add(2);
	        coords.add(coord.shift(0, -dist, dist));
	        directions.add(1);
	        coords.add(coord.shift(0, dist, dist));
	        directions.add(2);
	        coords.add(coord.shift(0, dist, -dist));
	        directions.add(1);
	    } else if (direction == 1) {
	        coords.add(coord.shift(-dist, 0, -dist));
	        directions.add(2);
	        coords.add(coord.shift(-dist, 0, dist));
	        directions.add(0);
	        coords.add(coord.shift(dist, 0, dist));
	        directions.add(2);
	        coords.add(coord.shift(dist, 0, -dist));
	        directions.add(0);
	    } else if (direction == 2) {
	        coords.add(coord.shift(-dist, -dist, 0));
	        directions.add(1);
	        coords.add(coord.shift(-dist, dist, 0));
	        directions.add(0);
	        coords.add(coord.shift(dist, dist, 0));
	        directions.add(1);
	        coords.add(coord.shift(dist, -dist, 0));
	        directions.add(0);
	    }

		drawEdge(buffer, color, coords.get(0), coords.get(1), thickness, directions.get(0));
		drawEdge(buffer, color, coords.get(1), coords.get(2), thickness, directions.get(1));
		drawEdge(buffer, color, coords.get(2), coords.get(3), thickness, directions.get(2));
		drawEdge(buffer, color, coords.get(3), coords.get(0), thickness, directions.get(3));
    }


    private static void drawEdge(BufferBuilder buffer, ArrayList<Float> color, Coordinate coord1, Coordinate coord2, float thickness, int direction) {
    	final float r = color.get(0) / 255f;
        final float g = color.get(1) / 255f;
        final float b = color.get(2) / 255f;
        final float a = color.get(3);

        float px = 0, py = 0, pz = 0;
        if (direction == 0) {
			px = thickness;
		} else if (direction == 1) {
			py = thickness;
		} else if (direction == 2) {
			pz = thickness;
		}

        buffer.vertex(coord1.x - px, coord1.y - py, coord1.z - pz).color(r, g, b, a).next();
        buffer.vertex(coord1.x + px, coord1.y + py, coord1.z + pz).color(r, g, b, a).next();
        buffer.vertex(coord2.x + px, coord2.y + py, coord2.z + pz).color(r, g, b, a).next();
        buffer.vertex(coord2.x - px, coord2.y - py, coord2.z - pz).color(r, g, b, a).next();
    }

    private static void renderBlockFaces(BufferBuilder buffer, BlockPos block, ArrayList<Float> color) {
        final float size = 1.0f;
        final float offset = 0.001f;

        final double x = block.getX();
        final double y = block.getY();
        final double z = block.getZ();

        float r = color.get(0) / 255f;
        float g = color.get(1) / 255f;
        float b = color.get(2) / 255f;
        float a = color.get(3);

        buffer.vertex(x - offset, y - offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y - offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y + size + offset, z + size + offset).color(r, g, b, a).next();

        buffer.vertex(x + size + offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y + size + offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z - offset).color(r, g, b, a).next();

        buffer.vertex(x - offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y - offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y + size + offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y + size + offset, z - offset).color(r, g, b, a).next();

        buffer.vertex(x + size + offset, y - offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z + size + offset).color(r, g, b, a).next();

        buffer.vertex(x - offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y - offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y - offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y - offset, z + size + offset).color(r, g, b, a).next();

        buffer.vertex(x - offset, y + size + offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z + size + offset).color(r, g, b, a).next();
        buffer.vertex(x + size + offset, y + size + offset, z - offset).color(r, g, b, a).next();
        buffer.vertex(x - offset, y + size + offset, z - offset).color(r, g, b, a).next();
    }

    public static class RenderEntry {
        public final ArrayList<Float> color;
        public final RenderMode mode;

        public RenderEntry(ArrayList<Float> color, RenderMode mode) {
            this.color = color;
            this.mode = mode;
        }
    }

    public enum RenderMode {
        OUTLINE,
        HIGHLIGHT
    }
}
