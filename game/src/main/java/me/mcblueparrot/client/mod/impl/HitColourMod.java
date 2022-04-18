package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.HitOverlayEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.data.Colour;

public class HitColourMod extends Mod {

	public static boolean enabled;
	public static HitColourMod instance;

	@Expose
	@Option
	public Colour colour = new Colour(255, 0, 0, 76);

	@Override
	public String getId() {
		return "hit_colour";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
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

	@EventHandler
	public void onHitOverlay(HitOverlayEvent event) {
		event.r = colour.getRedFloat();
		event.g = colour.getGreenFloat();
		event.b = colour.getBlueFloat();
		event.a = colour.getAlphaFloat();
	}

}
