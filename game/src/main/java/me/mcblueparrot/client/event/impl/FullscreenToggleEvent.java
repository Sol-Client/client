package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FullscreenToggleEvent {

	public final boolean state;
	public boolean applyState = true;
	public boolean cancelled;

}
