package com.sbc.render;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sbc.object.RenderEntry;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class RenderQueue {
	
    public static HashMap<UUID, RenderEntry> renderQueue;

    public static void init() {
        renderQueue = new HashMap<>();
        WorldRenderEvents.AFTER_ENTITIES.register(RenderQueue::render);
    }

    public static UUID add(RenderEntry entry) {
        UUID id = UUID.randomUUID();
        entry.setId(id);
        renderQueue.put(id, entry);
        return id;
    }

    public static void remove(UUID id) {
        renderQueue.remove(id);
    }

    public static synchronized void render(WorldRenderContext context) {
        if (renderQueue.isEmpty()) return;

        for (Map.Entry<UUID, RenderEntry> entry : renderQueue.entrySet()) {
        	//UUID id = entry.getKey();
            RenderEntry renderEntry = entry.getValue();

            if (renderEntry.color.getA() <= 0f) continue;

            switch (renderEntry.mode) {
				case FILLED:
					throw new IllegalStateException("RenderQueue: FILLED mode is not implemented yet.");
				case FILLED_THROUGH_WALLS:
					if (renderEntry.coords.size() == 1) {
						RenderHelper.renderFilled(context, renderEntry.coords.get(0), renderEntry.color, true);
					}
					else if (renderEntry.coords.size() == 2) {
						RenderHelper.renderFilled(context, renderEntry.coords.get(0), renderEntry.coords.get(1), renderEntry.color, true);
					}
					else {
						throw new IllegalStateException("Invalid coordinate count for FILLED_THROUGH_WALLS: " + renderEntry.coords.size() + " in " + renderEntry.toString());
					}
					break;
				case LINES:
					throw new IllegalStateException("RenderQueue: LINES mode is not implemented yet.");
				case LINES_THROUGH_WALLS:
					if (renderEntry.coords.size() == 1) {
						RenderHelper.renderOutline(context, renderEntry.coords.get(0), renderEntry.color, renderEntry.lineWidth, true);
					}
					else if (renderEntry.coords.size() == 2) {
						RenderHelper.renderOutline(context, renderEntry.coords.get(0), renderEntry.coords.get(1), renderEntry.color, renderEntry.lineWidth, true);
					}
					else {
						throw new IllegalStateException("Invalid coordinate count for LINES_THROUGH_WALLS: " + renderEntry.coords.size() + " in " + renderEntry.toString());
					}
					break;
			}
        }
    }
}
