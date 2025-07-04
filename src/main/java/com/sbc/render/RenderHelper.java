package com.sbc.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sbc.SkyblockCheats;
import com.sbc.object.Color;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

public class RenderHelper {
    private static final Identifier TRANSLUCENT_DRAW = Identifier.of(SkyblockCheats.NAMESPACE, "translucent_draw");
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final BufferAllocator ALLOCATOR = new BufferAllocator(1536);

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.addPhaseOrdering(Event.DEFAULT_PHASE, TRANSLUCENT_DRAW);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TRANSLUCENT_DRAW, RenderHelper::drawTranslucents);
    }
    
    // Render Filled
    
    public static void renderFilled(WorldRenderContext context, BlockPos pos, Color color, boolean throughWalls) {
        renderFilledInternal(context, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, color.getR(), color.getG(), color.getB(), color.getA(), throughWalls);
    }
    
    public static void renderFilled(WorldRenderContext context, BlockPos pos1, BlockPos pos2, Color color, boolean throughWalls) {
        renderFilledInternal(context, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), color.getR(), color.getG(), color.getB(), color.getA(), throughWalls);
    } 
    
    public static void renderFilled(WorldRenderContext context, Vec3d pos, Color color, boolean throughWalls) {
        renderFilledInternal(context, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, color.getR(), color.getG(), color.getB(), color.getA(), throughWalls);
    }
    
    public static void renderFilled(WorldRenderContext context, Vec3d pos1, Vec3d pos2, Color color, boolean throughWalls) {
        renderFilledInternal(context, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), color.getR(), color.getG(), color.getB(), color.getA(), throughWalls);
    } 
    
    public static void renderFilled(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, boolean throughWalls) {
    	renderFilledInternal(context, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha, throughWalls);
    }

    public static void renderFilledInternal(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, boolean throughWalls) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(throughWalls ? SBCRenderLayers.FILLED_THROUGH_WALLS : SBCRenderLayers.FILLED);

        VertexRendering.drawFilledBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);

        matrices.pop();
    }
    
    // Render Outline
    
    public static void renderOutline(WorldRenderContext context, BlockPos pos, Color color, float lineWidth, boolean throughWalls) {
        renderOutlineInternal(context, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, color.getR(), color.getG(), color.getB(), color.getA(), lineWidth, throughWalls);
    }

    public static void renderOutline(WorldRenderContext context, Vec3d pos, Color color, float lineWidth, boolean throughWalls) {
        renderOutlineInternal(context, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, color.getR(), color.getG(), color.getB(), color.getA(), lineWidth, throughWalls);
    }

    public static void renderOutline(WorldRenderContext context, Vec3d pos1, Vec3d pos2, Color color, float lineWidth, boolean throughWalls) {
        renderOutlineInternal(context, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), color.getR(), color.getG(), color.getB(), color.getA(), lineWidth, throughWalls);
    }

    public static void renderOutlineInternal(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float lineWidth, boolean throughWalls) {
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
        RenderLayer layer = throughWalls ? SBCRenderLayers.getLinesThroughWalls(lineWidth) : SBCRenderLayers.getLines(lineWidth);
        VertexConsumer buffer = consumers.getBuffer(layer);

        VertexRendering.drawBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        consumers.draw(layer);

        matrices.pop();
    }

    public static void renderLinesFromPoints(WorldRenderContext context, Vec3d[] points, Color color, float lineWidth, boolean throughWalls) {
        Vec3d camera = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MatrixStack.Entry entry = matrices.peek();

        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
        RenderLayer layer = throughWalls ? SBCRenderLayers.getLinesThroughWalls(lineWidth) : SBCRenderLayers.getLines(lineWidth);
        VertexConsumer buffer = consumers.getBuffer(layer);

        for (int i = 0; i < points.length - 1; i++) {
            Vec3d p1 = points[i];
            Vec3d p2 = points[i+1];
            Vector3f normalVec = p2.toVector3f().sub((float) p1.x, (float) p1.y, (float) p1.z).normalize();

            buffer.vertex(entry, (float) p1.x, (float) p1.y, (float) p1.z).color(color.getR(), color.getG(), color.getB(), color.getA()).normal(entry, normalVec);
            buffer.vertex(entry, (float) p2.x, (float) p2.y, (float) p2.z).color(color.getR(), color.getG(), color.getB(), color.getA()).normal(entry, normalVec);
        }

        consumers.draw(layer);
        matrices.pop();
    }

    // Unused


    public static void renderLineFromCursor(WorldRenderContext context, Vec3d point, float[] colorComponents, float alpha, float lineWidth) {
        Vec3d camera = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MatrixStack.Entry entry = matrices.peek();

        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
        RenderLayer layer = SBCRenderLayers.getLinesThroughWalls(lineWidth);
        VertexConsumer buffer = consumers.getBuffer(layer);

        // Start drawing the line from a point slightly in front of the camera
        Vec3d cameraPoint = camera.add(Vec3d.fromPolar(context.camera().getPitch(), context.camera().getYaw()));
        Vector3f normal = point.toVector3f().sub((float) cameraPoint.x, (float) cameraPoint.y, (float) cameraPoint.z).normalize();

        buffer
                .vertex(entry, (float) cameraPoint.x, (float) cameraPoint.y, (float) cameraPoint.z)
                .color(colorComponents[0], colorComponents[1], colorComponents[2], alpha)
                .normal(entry, normal);

        buffer
                .vertex(entry, (float) point.getX(), (float) point.getY(), (float) point.getZ())
                .color(colorComponents[0], colorComponents[1], colorComponents[2], alpha)
                .normal(entry, normal);

        consumers.draw(layer);
        matrices.pop();
    }

    public static void renderQuad(WorldRenderContext context, Vec3d[] points, float[] colorComponents, float alpha, boolean throughWalls) {
        Matrix4f positionMatrix = new Matrix4f();
        Vec3d camera = context.camera().getPos();

        positionMatrix.translate((float) -camera.x, (float) -camera.y, (float) -camera.z);

        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
        RenderLayer layer = throughWalls ? SBCRenderLayers.QUADS_THROUGH_WALLS : SBCRenderLayers.QUADS;
        VertexConsumer buffer = consumers.getBuffer(layer);

        for (int i = 0; i < 4; i++) {
            buffer.vertex(positionMatrix, (float) points[i].getX(), (float) points[i].getY(), (float) points[i].getZ()).color(colorComponents[0], colorComponents[1], colorComponents[2], alpha);
        }

        consumers.draw(layer);
    }

	/**
	 * Renders a texture in world space facing the player (like a name tag)
	 * @param context world render context
	 * @param pos world position
	 * @param width rendered width
	 * @param height rendered height
	 * @param textureWidth amount of texture rendered width
	 * @param textureHeight amount of texture rendered height
	 * @param renderOffset offset once it's been placed in the world facing the player
	 * @param texture reference to texture to render
	 * @param shaderColor color to apply to the texture (use white if none)
	 * @param throughWalls if it should render though walls
	 */
	public static void renderTextureInWorld(WorldRenderContext context, Vec3d pos, float width, float height, float textureWidth, float textureHeight, Vec3d renderOffset, Identifier texture, float[] shaderColor, float alpha, boolean throughWalls) {
		Matrix4f positionMatrix = new Matrix4f();
		Camera camera = context.camera();
		Vec3d cameraPos = camera.getPos();

		positionMatrix
				.translate((float) (pos.getX() - cameraPos.getX()), (float) (pos.getY() - cameraPos.getY()), (float) (pos.getZ() - cameraPos.getZ()))
				.rotate(camera.getRotation());

		VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
		RenderLayer layer = throughWalls ? SBCRenderLayers.getTextureThroughWalls(texture) : SBCRenderLayers.getTexture(texture);
		VertexConsumer buffer = consumers.getBuffer(layer);

		int color = ColorHelper.fromFloats(alpha, shaderColor[0], shaderColor[1], shaderColor[2]);

		buffer.vertex(positionMatrix, (float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()).texture(1, 1 - textureHeight).color(color);
		buffer.vertex(positionMatrix, (float) renderOffset.getX(), (float) renderOffset.getY() + height, (float) renderOffset.getZ()).texture(1, 1).color(color);
		buffer.vertex(positionMatrix, (float) renderOffset.getX() + width, (float) renderOffset.getY() + height	, (float) renderOffset.getZ()).texture(1 - textureWidth, 1).color(color);
		buffer.vertex(positionMatrix, (float) renderOffset.getX() + width, (float) renderOffset.getY(), (float) renderOffset.getZ()).texture(1 - textureWidth, 1 - textureHeight).color(color);

		consumers.draw(layer);
	}

    public static void renderText(WorldRenderContext context, Text text, Vec3d pos, boolean throughWalls) {
        renderText(context, text, pos, 1, throughWalls);
    }

    public static void renderText(WorldRenderContext context, Text text, Vec3d pos, float scale, boolean throughWalls) {
        renderText(context, text, pos, scale, 0, throughWalls);
    }

    public static void renderText(WorldRenderContext context, Text text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
        renderText(context, text.asOrderedText(), pos, scale, yOffset, throughWalls);
    }

    public static void renderText(WorldRenderContext context, OrderedText text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
        Matrix4f positionMatrix = new Matrix4f();
        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        TextRenderer textRenderer = CLIENT.textRenderer;

        scale *= 0.025f;

        positionMatrix
                .translate((float) (pos.getX() - cameraPos.getX()), (float) (pos.getY() - cameraPos.getY()), (float) (pos.getZ() - cameraPos.getZ()))
                .rotate(camera.getRotation())
                .scale(scale, -scale, scale);

        float xOffset = -textRenderer.getWidth(text) / 2f;

        VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(ALLOCATOR);

        textRenderer.draw(text, xOffset, yOffset, 0xFFFFFFFF, false, positionMatrix, consumers, throughWalls ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        consumers.draw();
    }

    public static void renderCylinder(WorldRenderContext context, Vec3d centre, float radius, float height, int segments, int color) {
    	MatrixStack matrices = context.matrixStack();
    	Vec3d camera = context.camera().getPos();

    	matrices.push();
    	matrices.translate(-camera.x, -camera.y, -camera.z);

    	VertexConsumer buffer = context.consumers().getBuffer(SBCRenderLayers.CYLINDER);
    	Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
    	float halfHeight = height / 2.0f;

    	for (int i = 0; i <= segments; i++) {
    		double angle = 2 * Math.PI * i / segments;
    		float dx = (float) Math.cos(angle) * radius;
    		float dz = (float) Math.sin(angle) * radius;

    		buffer.vertex(positionMatrix, (float) centre.getX() + dx, (float) centre.getY() + halfHeight, (float) centre.getZ() + dz).color(color);
    		buffer.vertex(positionMatrix, (float) centre.getX() + dx, (float) centre.getY() - halfHeight, (float) centre.getZ() + dz).color(color);
    	}

    	matrices.pop();
    }

    /**
     * This is called after all {@link WorldRenderEvents#AFTER_TRANSLUCENT} listeners have been called so that we can draw all remaining render layers.
     */
    private static void drawTranslucents(WorldRenderContext context) {
    	Profiler profiler = Profilers.get();

    	profiler.push("SBCTranslucentDraw");
    	VertexConsumerProvider.Immediate immediate = (VertexConsumerProvider.Immediate) context.consumers();

    	//Draw all render layers that haven't been drawn yet - drawing a specific layer does nothing and idk why (IF bug maybe?)
    	immediate.draw();
        profiler.pop();
    }

    public static void runOnRenderThread(Runnable runnable) {
        if (RenderSystem.isOnRenderThread()) {
        	runnable.run();
        } else {
            CLIENT.execute(runnable);
        }
        
    }
}