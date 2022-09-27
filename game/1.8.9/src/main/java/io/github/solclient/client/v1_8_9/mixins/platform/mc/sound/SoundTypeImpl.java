package io.github.solclient.client.v1_8_9.mixins.platform.mc.sound;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.sound.SoundType;

public class SoundTypeImpl {

}

@Mixin(SoundType.class)
interface SoundTypeImpl$Static {

	@Overwrite(remap = false)
	static @NotNull SoundType get(@NotNull String name) {
		switch(name) {
			case "BUTTON_CLICK":
				return new io.github.solclient.client.v1_8_9.platform.mc.sound.SoundTypeImpl(Identifier.minecraft("gui.button.press"));
		}

		throw new IllegalArgumentException(name);
	}

}