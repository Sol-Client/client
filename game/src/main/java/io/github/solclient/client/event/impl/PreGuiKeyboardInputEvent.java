package io.github.solclient.client.event.impl;

import io.github.solclient.client.annotation.ForgeCompat;

public class PreGuiKeyboardInputEvent {

	public boolean cancelled;

	@Deprecated
	@ForgeCompat
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
