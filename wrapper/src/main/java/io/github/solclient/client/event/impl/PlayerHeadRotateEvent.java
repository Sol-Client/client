package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerHeadRotateEvent {

	public final float yaw;
	public final float pitch;
	public boolean cancelled;

}
