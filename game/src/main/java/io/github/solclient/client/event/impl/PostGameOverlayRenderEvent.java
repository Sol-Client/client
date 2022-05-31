package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostGameOverlayRenderEvent {

	public final float partialTicks;
	public final GameOverlayElement type;

}
