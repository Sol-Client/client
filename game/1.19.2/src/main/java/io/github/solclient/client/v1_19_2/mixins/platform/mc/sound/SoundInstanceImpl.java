package io.github.solclient.client.v1_19_2.mixins.platform.mc.sound;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.sound.SoundInstance;
import io.github.solclient.client.platform.mc.sound.SoundType;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;

@Mixin(net.minecraft.client.sound.SoundInstance.class)
public interface SoundInstanceImpl extends SoundInstance {

}

@Mixin(SoundInstance.class)
interface SoundInstanceImpl$Static {

	@Helper
	@Overwrite(remap = false)
	static @NotNull SoundInstance ui(SoundType sound, float pitch) {
		return (SoundInstance) PositionedSoundInstance.master((SoundEvent) sound, pitch);
	}

}