package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.GammaEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public class NightVisionMod extends Mod {

    public NightVisionMod() {
        super("Fullbright", "nightVision", "Illuminate the entire world.", ModCategory.VISUAL);
    }

    @EventHandler
    public void onGamma(GammaEvent event) {
        event.gamma = 20F;
    }

}
