package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostGameOverlayRenderEvent {

	public float partialTicks;
	public GameOverlayElement type;

}
