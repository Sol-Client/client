package io.github.solclient.abstraction.mc.sound;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

public interface SoundType {

	SoundType BUTTON_CLICK = null;

	@NotNull Identifier getId();

}
