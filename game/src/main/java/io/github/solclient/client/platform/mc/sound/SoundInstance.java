package io.github.solclient.client.platform.mc.sound;

import org.jetbrains.annotations.NotNull;

public interface SoundInstance {

	static @NotNull SoundInstance ui(SoundType sound, float pitch) {
		throw new UnsupportedOperationException();
	}

}
