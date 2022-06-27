package io.github.solclient.abstraction.mc.hud.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.text.Text;

public interface ChatMessage {

	int getCreationTick();

	@NotNull Text getMessage();

	int getUpdatedCounter();

}
