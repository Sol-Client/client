package me.mcblueparrot.client.event.impl;

import me.mcblueparrot.client.annotation.ForgeCompat;

public class PreGuiKeyboardInputEvent {

	public boolean cancelled;

	@Deprecated
	@ForgeCompat
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
