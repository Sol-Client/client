package io.github.solclient.client.event.impl.network.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.text.Text;

public class ActionBarPlayEvent extends IncomingChatMessageEvent {

	public ActionBarPlayEvent(@NotNull Text message) {
		super(message);
	}

}
