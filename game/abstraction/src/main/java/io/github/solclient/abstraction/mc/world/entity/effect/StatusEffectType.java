package io.github.solclient.abstraction.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

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
