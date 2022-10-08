package io.github.solclient.client.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;

public interface StatusEffectType {

	StatusEffectType SPEED = get("SPEED"),
			STRENGTH = get("STRENGTH"),
			BLINDNESS = get("BLINDNESS");

	static StatusEffectType get(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Renders the status effect icon in the GUI.
	 * @param x The x position.
	 * @param y The y position.
	 */
	void render(int x, int y);

	@NotNull String getName();

}
