package com.sbc.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtil {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Map<Object, RenderObject> renderObjects = new ConcurrentHashMap<>();

    public static void init() {
    	WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            MatrixStack matrices = context.matrixStack();
            render(matrices, context.consumers());
        });
    }
    
    public static Object drawBox(BlockPos from, BlockPos to, int color, boolean renderThroughBlocks, int timer) {
        RenderObject box = new RenderObject(RenderType.BOX, from, to, color, renderThroughBlocks, timer);
        renderObjects.put(box, box);
        return box;
    }

    public static Object drawLine(Vec3d from, Vec3d to, int color, boolean renderThroughBlocks, int timer) {
        RenderObject line = new RenderObject(RenderType.LINE, from, to, color, renderThroughBlocks, timer);
        renderObjects.put(line, line);
        return line;
    }

    public static Object drawSphere(Vec3d center, double radius, int color, boolean renderThroughBlocks, int timer) {
        RenderObject sphere = new RenderObject(RenderType.SPHERE, center, radius, color, renderThroughBlocks, timer);
        renderObjects.put(sphere, sphere);
        return sphere;
    }

    public static void remove(Object obj) {
        if (obj instanceof RenderObject) {
			renderObjects.remove(obj);
		}
    }

    public static void removeAll() {
        renderObjects.clear();
    }

    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        long currentTime = System.currentTimeMillis();
        Iterator<RenderObject> iterator = renderObjects.values().iterator();

        while (iterator.hasNext()) {
        	System.out.println("Rendering objects...");
            RenderObject obj = iterator.next();
            if (obj.timer > 0 && currentTime > obj.creationTime + obj.timer) {
                iterator.remove();
                continue;
            }

            if (obj.renderThroughBlocks) {
                RenderSystem.disableDepthTest();
            }

            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
            obj.render(matrices, consumer);

            if (obj.renderThroughBlocks) {
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static class RenderObject {
        private final RenderType type;
        private final Object from;
        private final Object to;
        private final int color;
        private final boolean renderThroughBlocks;
        private final long creationTime;
        private final int timer;

        RenderObject(RenderType type, Object from, Object to, int color, boolean renderThroughBlocks, int timer) {
            this.type = type;
            this.from = from;
            this.to = to;
            this.color = color;
            this.renderThroughBlocks = renderThroughBlocks;
            this.creationTime = System.currentTimeMillis();
            this.timer = timer;
        }

        void render(MatrixStack matrices, VertexConsumer consumer) {
            switch (type) {
                case BOX -> renderBox(matrices, consumer);
                case LINE -> renderLine(matrices, consumer);
                case SPHERE -> renderSphere(matrices, consumer);
            }
        }

        private void renderBox(MatrixStack matrices, VertexConsumer consumer) {
            if (from instanceof BlockPos && to instanceof BlockPos) {
                BlockPos start = (BlockPos) from;
                BlockPos end = (BlockPos) to;

                double minX = Math.min(start.getX(), end.getX());
                double minY = Math.min(start.getY(), end.getY());
                double minZ = Math.min(start.getZ(), end.getZ());
                double maxX = Math.max(start.getX(), end.getX()) + 1;
                double maxY = Math.max(start.getY(), end.getY()) + 1;
                double maxZ = Math.max(start.getZ(), end.getZ()) + 1;

                // Draw edges of the box
                drawLine(matrices, consumer, minX, minY, minZ, maxX, minY, minZ, color);
                drawLine(matrices, consumer, minX, minY, minZ, minX, maxY, minZ, color);
                drawLine(matrices, consumer, minX, minY, minZ, minX, minY, maxZ, color);
                drawLine(matrices, consumer, maxX, maxY, maxZ, minX, maxY, maxZ, color);
                drawLine(matrices, consumer, maxX, maxY, maxZ, maxX, minY, maxZ, color);
                drawLine(matrices, consumer, maxX, maxY, maxZ, maxX, maxY, minZ, color);
                drawLine(matrices, consumer, minX, maxY, minZ, maxX, maxY, minZ, color);
                drawLine(matrices, consumer, minX, maxY, minZ, minX, maxY, maxZ, color);
                drawLine(matrices, consumer, maxX, minY, minZ, maxX, maxY, minZ, color);
                drawLine(matrices, consumer, maxX, minY, minZ, maxX, minY, maxZ, color);
                drawLine(matrices, consumer, minX, minY, maxZ, maxX, minY, maxZ, color);
                drawLine(matrices, consumer, minX, minY, maxZ, minX, maxY, maxZ, color);
            }
        }

        private void renderLine(MatrixStack matrices, VertexConsumer consumer) {
            if (from instanceof Vec3d && to instanceof Vec3d) {
                Vec3d start = (Vec3d) from;
                Vec3d end = (Vec3d) to;

                drawLine(matrices, consumer, start.x, start.y, start.z, end.x, end.y, end.z, color);
            }
        }

        private void renderSphere(MatrixStack matrices, VertexConsumer consumer) {
            if (from instanceof Vec3d && to instanceof Double) {
                Vec3d center = (Vec3d) from;
                double radius = (Double) to;

                int segments = 24; // Number of segments for the sphere
                for (int i = 0; i < segments; i++) {
                    double theta1 = 2.0 * Math.PI * i / segments;
                    double theta2 = 2.0 * Math.PI * (i + 1) / segments;

                    for (int j = 0; j < segments; j++) {
                        double phi1 = Math.PI * j / segments;
                        double phi2 = Math.PI * (j + 1) / segments;

                        double x1 = center.x + radius * Math.sin(phi1) * Math.cos(theta1);
                        double y1 = center.y + radius * Math.cos(phi1);
                        double z1 = center.z + radius * Math.sin(phi1) * Math.sin(theta1);

                        double x2 = center.x + radius * Math.sin(phi1) * Math.cos(theta2);
                        double y2 = center.y + radius * Math.cos(phi1);
                        double z2 = center.z + radius * Math.sin(phi1) * Math.sin(theta2);

                        double x3 = center.x + radius * Math.sin(phi2) * Math.cos(theta1);
                        double y3 = center.y + radius * Math.cos(phi2);
                        double z3 = center.z + radius * Math.sin(phi2) * Math.sin(theta1);

                        drawLine(matrices, consumer, x1, y1, z1, x2, y2, z2, color);
                        drawLine(matrices, consumer, x1, y1, z1, x3, y3, z3, color);
                    }
                }
            }
        }

        private void drawLine(MatrixStack matrices, VertexConsumer consumer, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            float alpha = (color >> 24 & 255) / 255.0F;

            consumer.vertex(matrices.peek().getPositionMatrix(), (float) x1, (float) y1, (float) z1)
            .color(red, green, blue, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .next();

            consumer.vertex(matrices.peek().getPositionMatrix(), (float) x2, (float) y2, (float) z2)
            .color(red, green, blue, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .next();
            //consumer.vertex(matrices.peek().getPositionMatrix(), (float) x1, (float) y1, (float) z1).color(red, green, blue, alpha).next();
            //consumer.vertex(matrices.peek().getPositionMatrix(), (float) x2, (float) y2, (float) z2).color(red, green, blue, alpha).next();
        }
    }

    private enum RenderType {
        BOX, LINE, SPHERE
    }
}
