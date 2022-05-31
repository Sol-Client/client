package io.github.solclient.client.event.impl;

import io.github.solclient.client.annotation.ForgeCompat;
import lombok.RequiredArgsConstructor;

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
