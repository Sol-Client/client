package io.github.solclient.client.platform.mc.sound;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;

public interface SoundInstance {

	@Helper
	static @NotNull SoundInstance ui(SoundType sound, float pitch) {
		throw new UnsupportedOperationException();
	}

}
