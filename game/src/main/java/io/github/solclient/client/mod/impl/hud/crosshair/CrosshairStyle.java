package io.github.solclient.client.mod.impl.hud.crosshair;

import io.github.solclient.client.platform.mc.lang.I18n;

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
		return I18n.translate("sol_client.mod.crosshair.option.style." + name().toLowerCase());
	}

}