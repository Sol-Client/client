package io.github.solclient.client.platform.mc.hud.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.text.Text;

public interface ChatMessage {

	int getCreationTick();

	@NotNull Text getMessage();

	int getUpdatedCounter();

}
