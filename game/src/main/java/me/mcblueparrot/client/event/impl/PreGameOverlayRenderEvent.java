package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.annotation.ForgeCompat;

@RequiredArgsConstructor
public class PreGameOverlayRenderEvent {

	public final float partialTicks;
	public final GameOverlayElement type;
	public boolean cancelled;

	@Deprecated
	@ForgeCompat
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
