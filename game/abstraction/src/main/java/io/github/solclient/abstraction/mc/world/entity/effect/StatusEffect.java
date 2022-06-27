package io.github.solclient.abstraction.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

/**
 * Represents an instance of a status effect.
 */
public interface StatusEffect {

	static StatusEffect create(StatusEffectType type) {
		throw new UnsupportedOperationException();
	}

	static StatusEffect create(StatusEffectType type, int duration) {
		throw new UnsupportedOperationException();
	}

	static StatusEffect create(StatusEffectType type, int duration, int amplifier) {
		throw new UnsupportedOperationException();
	}

	@NotNull StatusEffectType getType();

	int getDuration();

	@NotNull String getDurationText();

	int getAmplifier();

	boolean showIcon();

	@NotNull String getAmplifierName();

}
