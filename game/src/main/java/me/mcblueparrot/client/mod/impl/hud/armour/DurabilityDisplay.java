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
			return 73;
		case REMAINING:
			return 40;
		case PERCENTAGE:
			return 45;
		default:
			return 0;
		}
	}

}
