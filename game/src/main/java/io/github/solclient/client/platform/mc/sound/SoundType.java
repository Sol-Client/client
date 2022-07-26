package io.github.solclient.client.platform.mc.sound;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.resource.Identifier;

public interface SoundType {

	SoundType BUTTON_CLICK = null;

	@NotNull Identifier getId();

}
