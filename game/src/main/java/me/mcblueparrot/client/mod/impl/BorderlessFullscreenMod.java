package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public class BorderlessFullscreenMod extends Mod {

	public static boolean enabled;

	public BorderlessFullscreenMod() {
		super("Borderless Fullscreen", "borderless_fullscreen", "Improve fullscreen mode.", ModCategory.UTILITY);
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

}
