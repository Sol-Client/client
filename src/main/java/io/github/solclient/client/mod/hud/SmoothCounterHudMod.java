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

package io.github.solclient.client.mod.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.mod.option.annotation.*;

@AbstractTranslationKey("sol_client.mod.smooth_counter_hud")
public abstract class SmoothCounterHudMod extends SolClientSimpleHudMod {

	@Expose
	@Option
	private boolean smoothNumbers = true;

	public abstract int getIntValue();

	public abstract String getSuffix();

	private int counter;

	@EventHandler
	public void onTick(PostTickEvent event) {
		if (mc.world == null)
			return;

		int actualValue = getIntValue();

		if (!smoothNumbers) {
			counter = actualValue;
			return;
		}

		if (actualValue > counter) {
			counter += Math.max(((actualValue - counter) / 2), 1);
		} else if (actualValue < counter) {
			counter -= Math.max(((counter - actualValue) / 2), 1);
		}
	}

	@Override
	public String getText(boolean editMode) {
		if (editMode) {
			return "0 " + getSuffix();
		} else {
			return counter + " " + getSuffix();
		}
	}

}
