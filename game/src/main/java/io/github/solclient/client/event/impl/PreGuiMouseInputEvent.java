package io.github.solclient.client.event.impl;

import io.github.solclient.client.util.ForgeCompat;

public class PreGuiMouseInputEvent {

	public boolean cancelled;

	@Deprecated
	@ForgeCompat
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
