package io.github.solclient.client.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

public interface StatusEffectType {

	StatusEffectType SPEED = get("SPEED");
	StatusEffectType STRENGTH = get("STRENGTH");
	StatusEffectType BLINDNESS = get("BLINDNESS");

	static StatusEffectType get(String name) {
		throw new UnsupportedOperationException();
	}

	float getAtlasU();

	float getAtlasV();

	@NotNull String getName();

}
