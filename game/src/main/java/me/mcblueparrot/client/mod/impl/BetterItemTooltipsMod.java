package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public class BetterItemTooltipsMod extends Mod {

	public static boolean enabled;

	public BetterItemTooltipsMod() {
		super("Better Tooltips", "better_tootips", "More detailed item tooltips.", ModCategory.UTILITY);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
