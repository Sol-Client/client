package io.github.solclient.client.v1_8_9.mixins.platform.mc.sound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.sound.SoundEngine;
import io.github.solclient.client.platform.mc.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;

@Mixin(SoundManager.class)
public abstract class SoundEngineImpl implements SoundEngine {

	@Override
	public void play(SoundInstance sound) {
		play((net.minecraft.client.sound.SoundInstance) sound);
	}

	@Shadow
	public abstract void play(net.minecraft.client.sound.SoundInstance sound);

}
