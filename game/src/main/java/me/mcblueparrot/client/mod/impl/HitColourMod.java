package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.HitOverlayEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Colour;

public class HitColourMod extends Mod {

    public static boolean enabled;
    public static HitColourMod instance;

    @Expose
    @ConfigOption("Colour")
    public Colour colour = new Colour(255, 0, 0, 76);

    public HitColourMod() {
        super("Hit Colour", "hit_colour", "Customise hit colour.", ModCategory.VISUAL);
        HitColourMod.instance = this;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        enabled = true;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        enabled = true;
    }

    @EventHandler
    public void onHitOverlay(HitOverlayEvent event) {
        event.r = colour.getRedFloat();
        event.g = colour.getGreenFloat();
        event.b = colour.getBlueFloat();
        event.a = colour.getAlphaFloat();
    }

}
