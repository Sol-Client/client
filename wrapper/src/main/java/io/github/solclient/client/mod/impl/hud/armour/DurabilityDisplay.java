package io.github.solclient.client.mod.impl.hud.armour;

import net.minecraft.client.resources.I18n;

public enum DurabilityDisplay {
	OFF, FRACTION, REMAINING, PERCENTAGE;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.armour.option.durability." + name().toLowerCase());
	}

	public int getWidth() {
		switch (this) {
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
