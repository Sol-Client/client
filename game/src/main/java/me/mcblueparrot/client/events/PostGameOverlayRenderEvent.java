package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostGameOverlayRenderEvent {

	public float partialTicks;
	public GameOverlayElement type;

}
