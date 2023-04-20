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

package io.github.solclient.client.mod.impl.tnttimer;

import java.text.DecimalFormat;

import io.github.solclient.client.DetectedServer;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Formatting;

public class TNTTimerMod extends StandardMod {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	public static boolean enabled;

	// Unfortunately doesn't work with TNT chains due to their random nature.
	public static String getText(TntEntity tnt) {
		float fuse = tnt.fuseTimer;

		// Based on Sk1er's mod
		if (DetectedServer.current() == DetectedServer.HYPIXEL && "BED WARS".equals(MinecraftUtils.getScoreboardTitle())) {
			fuse -= 28;
		}

		Formatting colour = Formatting.GREEN;

		if (fuse < 20) {
			colour = Formatting.DARK_RED;
		} else if (fuse < 40) {
			colour = Formatting.RED;
		} else if (fuse < 60) {
			colour = Formatting.GOLD;
		}

		return colour + FORMAT.format(fuse / 20);
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
