package io.github.solclient.abstraction.mc.hud;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.hud.chat.Chat;

public interface IngameHud {

	@NotNull Chat getChat();

	int getTicks();

}
