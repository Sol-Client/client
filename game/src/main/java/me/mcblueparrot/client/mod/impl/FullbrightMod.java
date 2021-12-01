package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GammaEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public class FullbrightMod extends Mod {

	public FullbrightMod() {
		super("Fullbright", "nightVision", "Illuminate the entire world.", ModCategory.VISUAL);
	}

	@EventHandler
	public void onGamma(GammaEvent event) {
		event.gamma = 20F;
	}

}
