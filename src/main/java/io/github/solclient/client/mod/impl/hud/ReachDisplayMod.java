/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.hud;

import java.text.DecimalFormat;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.EntityAttackEvent;
import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;

public class ReachDisplayMod extends SolClientSimpleHudMod {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

	private double distance = 0;
	private long hitTime = -1;

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
