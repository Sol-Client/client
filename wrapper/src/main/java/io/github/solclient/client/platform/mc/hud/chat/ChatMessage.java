package io.github.solclient.client.platform.mc.hud.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.text.OrderedText;

public interface ChatMessage {

	int getMessageCreationTick();

	@NotNull OrderedText getMessage();

}
