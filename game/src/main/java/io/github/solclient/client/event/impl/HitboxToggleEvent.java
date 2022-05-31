package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HitboxToggleEvent {

	public final boolean state;
	public boolean cancelled;

}
