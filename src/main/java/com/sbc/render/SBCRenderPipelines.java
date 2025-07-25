package com.sbc.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.sbc.SkyblockCheats;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class SBCRenderPipelines {
	/** Similar to {@link RenderPipelines#DEBUG_FILLED_BOX} */
	public static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/debug_filled_box_through_walls"))
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());
	/** Similar to {@link RenderPipelines#LINES} */
	public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/lines_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());
	/** Similar to {@link RenderPipelines#DEBUG_QUADS}  */
	public static final RenderPipeline QUADS_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/debug_quads_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.build());
	/** Similar to {@link RenderPipelines#GUI_TEXTURED} */
	public static final RenderPipeline TEXTURE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/texture"))
			.withCull(false)
			.build());
	public static final RenderPipeline TEXTURE_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/texture_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.build());
	public static final RenderPipeline CYLINDER = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation(Identifier.of(SkyblockCheats.NAMESPACE, "pipeline/cylinder"))
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
			.withCull(false)
			.build());
	
	public static void init() {}
}