package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public class ShowOwnTagMod extends Mod {

	// stub mod implementation

	public static boolean enabled;

	public ShowOwnTagMod() {
		super("Show own Tag", "show_own_tag", "Show your own nametag.", ModCategory.UTILITY);
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
