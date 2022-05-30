package me.mcblueparrot.client.mod.impl.hud.armour;

import net.minecraft.client.resources.I18n;

public enum DurabilityDisplay {
	FRACTION,
	REMAINING,
	PERCENTAGE;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.armour.option.durability." + name().toLowerCase());
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
