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

package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.lib.penner.easing.*;
import io.github.solclient.client.ui.component.Component;

public class AnimatedFloatController implements Controller<Float> {

	private final Controller<Float> base;
	private final int duration;
	private Float last;
	private float current;
	private long currentTime;

	public AnimatedFloatController(Controller<Float> base, int duration) {
		this.base = base;
		this.duration = duration;
	}

	@Override
	public Float get(Component component, Float defaultValue) {
		float baseValue = base.get(component, defaultValue);
		if (baseValue != current) {
			if (last != null)
				currentTime = System.currentTimeMillis();
			last = current;
			current = baseValue;
		}

		if (last == null)
			last = current;

		return Sine.easeOut(Math.min(System.currentTimeMillis() - currentTime, duration), last, current - last, duration);
	}

}
