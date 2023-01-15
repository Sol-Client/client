package io.github.solclient.client.mod.impl.hud;

import java.text.DecimalFormat;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.EntityAttackEvent;
import io.github.solclient.client.mod.hud.SimpleHudMod;

public class ReachDisplayMod extends SimpleHudMod {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

	private double distance = 0;
	private long hitTime = -1;

	@Override
	public String getId() {
		return "reach_display";
	}

	@Override
	public String getText(boolean editMode) {
		if ((System.currentTimeMillis() - hitTime) > 5000) {
			distance = 0;
		}
		if (editMode) {
			return "0 mts";
		} else {
			return FORMAT.format(distance) + " m" + (distance != 1.0 ? "ts" : "");
		}
	}

	@EventHandler
	public void totallyNoReachHax(EntityAttackEvent event) {
		if (mc.result != null && mc.result.pos != null) {
			distance = mc.result.pos.distanceTo(mc.player.getCameraPosVec(1.0F));
			hitTime = System.currentTimeMillis();
		}
	}

}
