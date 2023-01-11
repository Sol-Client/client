package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiNewChat;

@RequiredArgsConstructor
public class ChatRenderEvent {

	public final GuiNewChat chat;
	public final int updateCounter;
	public final float partialTicks;
	public boolean cancelled;

}
