package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostGameOverlayRenderEvent {

	public final float partialTicks;
	public final GameOverlayElement type;

}
