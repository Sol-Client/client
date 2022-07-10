package io.github.solclient.client.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.Identifier;

public interface StatusEffectType {

	StatusEffectType SPEED = null;
	StatusEffectType STRENGTH = null;
	StatusEffectType BLINDNESS = null;

	static String getDurationString(int ticks) {
		throw new UnsupportedOperationException();
	}

	@NotNull Identifier getId();

	float getAtlasU();

	float getAtlasV();

	@NotNull String getName();

}
