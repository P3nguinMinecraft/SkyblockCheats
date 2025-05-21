package com.sbc.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class RenderOutlines {
    private static VertexBuffer vertexBuffer;
    public static AtomicBoolean requestedRefresh = new AtomicBoolean(false);
    private static int canvasLoaded = -1;
    public static HashMap<BlockPos, ArrayList<Float>> renderQueue;
    
    public static void init() {
        renderQueue = new HashMap<>();
        WorldRenderEvents.BEFORE_ENTITIES.register(RenderOutlines::render);
    }

    public static void addBlock(BlockPos block, ArrayList<Float> color) {
        requestedRefresh.set(true);
        renderQueue.put(block, color);
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
            RenderSystem.lineWidth(5.0F);

            buffer.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            for (BlockPos block : renderQueue.keySet()) {
                renderBlock(buffer, block, renderQueue.get(block));
            }

            vertexBuffer.bind();
            vertexBuffer.upload(buffer.end());
            VertexBuffer.unbind();
        }

        if (vertexBuffer != null) {
            Camera camera = context.camera();
            Vec3d cameraPos = camera.getPos();

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

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

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            Matrix4f modelMatrix = matrices.peek().getPositionMatrix();
            Matrix4f projectionMatrix = new Matrix4f(context.projectionMatrix());

            vertexBuffer.bind();
            vertexBuffer.draw(modelMatrix, projectionMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            matrices.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private static void renderBlock(BufferBuilder buffer, BlockPos block, ArrayList<Float> color) {
        final float size = 1.0f;
        final double x = block.getX(), y = block.getY(), z = block.getZ();
        
        float red = color.get(0) / 255f;
        float green = color.get(1) / 255f;
        float blue = color.get(2) / 255f;
        float opacity = color.get(3);
        
        System.out.println("[DEBUG] Color: " + red + ", " + green + ", " + blue + ", " + opacity);
        
        if (opacity == 0f) {
            ChatUtils.sendMessage("§cWarning: Block color has zero alpha (fully transparent). It will not be visible.");
        }
        
        // Top face
        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();
        
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();

        // Bottom face
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();

        // Vertical edges
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();
        
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();
        
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();
    }
}