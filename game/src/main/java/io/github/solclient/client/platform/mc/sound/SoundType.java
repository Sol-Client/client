package io.github.solclient.client.platform.mc.sound;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.resource.Identifier;

public interface SoundType {

	SoundType BUTTON_CLICK = get("BUTTON_CLICK");

	@NotNull Identifier getId();

	static @NotNull SoundType get(@NotNull String name) {
		throw new UnsupportedOperationException();
	}

}
