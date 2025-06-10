package com.sbc.object;

import java.util.UUID;

public class UniqueAction {
	public final UUID id;
	public boolean active = true;
	private final Runnable action;
	private final Runnable onRun;

	public UniqueAction(Runnable action, UUID uuid) {
		this.action = action;
		this.onRun = null;
		this.id = uuid;
	}
	
	public UniqueAction(Runnable action, UUID uuid, Runnable onRun) {
		this.action = action;
		this.onRun = onRun;
		this.id = uuid;
	}
	
	public void run() {
		if (!active) {
			return;
		}
		action.run();
		if (onRun != null) {
			onRun.run();
		}
		active = false;
	}
}
