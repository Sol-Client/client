package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.hud.ChatHud;

@RequiredArgsConstructor
public class ChatRenderEvent {

	public final ChatHud chat;
	public final int ticks;
	public final float partialTicks;
	public boolean cancelled;

}
