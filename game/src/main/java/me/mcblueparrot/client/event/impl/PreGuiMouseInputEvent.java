package me.mcblueparrot.client.event.impl;

import me.mcblueparrot.client.annotation.ForgeCompat;

public class PreGuiMouseInputEvent {

	public boolean cancelled;

	@Deprecated
	@ForgeCompat
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
