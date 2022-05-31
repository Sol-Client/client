package io.github.solclient.client.mod.impl.hud.crosshair;

import net.minecraft.client.resources.I18n;

public enum CrosshairStyle {
	DEFAULT,
	NONE,
	DOT,
	PLUS,
	PLUS_DOT,
	SQUARE,
	SQUARE_DOT,
	CIRCLE,
	CIRCLE_DOT,
	FOUR_ANGLED,
	FOUR_ANGLED_DOT,
	TRIANGLE;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.crosshair.option.style." + name().toLowerCase());
	}

}