package io.github.solclient.client.platform.mc.hud;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.hud.chat.Chat;

public interface IngameHud {

	@NotNull Chat getChat();

	int getTickCounter();

}
