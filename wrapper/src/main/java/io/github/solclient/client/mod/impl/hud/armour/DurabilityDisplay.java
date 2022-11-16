package io.github.solclient.client.mod.impl.hud.armour;

import io.github.solclient.client.platform.mc.lang.I18n;

public enum DurabilityDisplay {
	OFF,
	FRACTION,
	REMAINING,
	PERCENTAGE;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.armour.option.durability." + name().toLowerCase());
	}

	public int getWidth() {
		switch(this) {
			case FRACTION:
				return 67;
			case REMAINING:
				return 29;
			case PERCENTAGE:
				return 30;
			default:
				return 0;
		}
	}

}
