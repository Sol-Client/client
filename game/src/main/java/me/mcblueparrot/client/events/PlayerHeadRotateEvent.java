package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerHeadRotateEvent {

	public final float yaw;
	public final float pitch;
	public boolean cancelled;

}
