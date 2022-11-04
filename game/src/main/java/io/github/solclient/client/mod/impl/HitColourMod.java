package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.entity.render.HitOverlayRenderEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.data.Colour;

public final class HitColourMod extends Mod {

	public static final HitColourMod INSTANCE = new HitColourMod();

	@Expose
	@Option
	private Colour colour = new Colour(255, 0, 0, 76);

	@Override
	public String getId() {
		return "hit_colour";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onHitOverlay(HitOverlayRenderEvent event) {
		event.setR(colour.getRedFloat());
		event.setG(colour.getGreenFloat());
		event.setB(colour.getBlueFloat());
		event.setA(colour.getAlphaFloat());
	}

}
