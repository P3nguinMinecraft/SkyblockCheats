package com.sbc.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sbc.util.ChatUtils;

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
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public class Render {
    private static VertexBuffer vertexBuffer;
    public static AtomicBoolean requestedRefresh = new AtomicBoolean(false);
    private static int canvasLoaded = -1;

    public static HashMap<BlockPos, RenderEntry> renderQueue;

    public static void init() {
        renderQueue = new HashMap<>();
        WorldRenderEvents.BEFORE_ENTITIES.register(Render::render);
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
        final float size = 1.0f;
        final float offset = 0.001f;

        final double x = block.getX();
        final double y = block.getY();
        final double z = block.getZ();

        float r = color.get(0) / 255f;
        float g = color.get(1) / 255f;
        float b = color.get(2) / 255f;
        float a = color.get(3);

        if (a == 0f) {
            ChatUtils.sendMessage("§cWarning: Block color has zero alpha (fully transparent). It will not be visible.");
        }

        drawEdge(buffer, x - offset, y - offset, z - offset, x + size + offset, y - offset, z - offset, r, g, b, a);
        drawEdge(buffer, x - offset, y - offset, z + size + offset, x + size + offset, y - offset, z + size + offset, r, g, b, a);
        drawEdge(buffer, x - offset, y - offset, z - offset, x - offset, y - offset, z + size + offset, r, g, b, a);
        drawEdge(buffer, x + size + offset, y - offset, z - offset, x + size + offset, y - offset, z + size + offset, r, g, b, a);

        drawEdge(buffer, x - offset, y + size + offset, z - offset, x + size + offset, y + size + offset, z - offset, r, g, b, a);
        drawEdge(buffer, x - offset, y + size + offset, z + size + offset, x + size + offset, y + size + offset, z + size + offset, r, g, b, a);
        drawEdge(buffer, x - offset, y + size + offset, z - offset, x - offset, y + size + offset, z + size + offset, r, g, b, a);
        drawEdge(buffer, x + size + offset, y + size + offset, z - offset, x + size + offset, y + size + offset, z + size + offset, r, g, b, a);

        drawEdge(buffer, x - offset, y - offset, z - offset, x - offset, y + size + offset, z - offset, r, g, b, a);
        drawEdge(buffer, x + size + offset, y - offset, z - offset, x + size + offset, y + size + offset, z - offset, r, g, b, a);
        drawEdge(buffer, x - offset, y - offset, z + size + offset, x - offset, y + size + offset, z + size + offset, r, g, b, a);
        drawEdge(buffer, x + size + offset, y - offset, z + size + offset, x + size + offset, y + size + offset, z + size + offset, r, g, b, a);
    }

    private static void drawEdge(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2,
            float r, float g, float b, float a) {
        final float thickness = 0.002f;

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        boolean isVertical = Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > Math.abs(dz);

        double px, py, pz;

        if (isVertical) {
            px = thickness;
            py = 0;
            pz = 0;
        } else {
            px = 0;
            py = thickness;
            pz = 0;
        }

        buffer.vertex(x1 - px, y1 - py, z1 - pz).color(r, g, b, a).next();
        buffer.vertex(x1 + px, y1 + py, z1 + pz).color(r, g, b, a).next();
        buffer.vertex(x2 + px, y2 + py, z2 + pz).color(r, g, b, a).next();
        buffer.vertex(x2 - px, y2 - py, z2 - pz).color(r, g, b, a).next();
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

        if (a == 0f) {
            ChatUtils.sendMessage("§cWarning: Block color has zero alpha (fully transparent). It will not be visible.");
        }

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
