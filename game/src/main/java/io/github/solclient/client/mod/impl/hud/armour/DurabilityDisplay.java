package io.github.solclient.client.mod.impl.hud.armour;

import io.github.solclient.abstraction.mc.lang.I18n;

public enum DurabilityDisplay {
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
			return 84;
		case REMAINING:
			return 46;
		case PERCENTAGE:
			return 47;
		default:
			return 0;
		}
	}

}
