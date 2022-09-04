package io.github.solclient.client.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

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

	int getEffectDuration();

	@NotNull String getDurationText();

	int getEffectAmplifier();

	boolean showIcon();

	boolean showAmplifier();

	@NotNull String getAmplifierName();

}
