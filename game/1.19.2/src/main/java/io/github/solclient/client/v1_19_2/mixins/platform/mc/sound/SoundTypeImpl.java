package io.github.solclient.client.v1_19_2.mixins.platform.mc.sound;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.sound.SoundType;
import net.minecraft.sound.*;

@Mixin(SoundEvent.class)
@Implements(@Interface(iface = SoundType.class, prefix = "platform$"))
public abstract class SoundTypeImpl {

	public @NotNull Identifier platform$getId() {
		return (Identifier) getId();
	}

	@Shadow
	public abstract net.minecraft.util.Identifier getId();

}

@Mixin(SoundType.class)
interface SoundTypeImpl$Static {

	@Overwrite(remap = false)
	static @NotNull SoundType get(@NotNull String name) {
		switch(name) {
			case "BUTTON_CLICK":
				return (SoundType) SoundEvents.UI_BUTTON_CLICK;
		}

		throw new IllegalArgumentException(name);
	}

}