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

import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.util.data.Colour;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnimatedColourController implements Controller<Colour> {

	private final Controller<Colour> base;
	private final int duration;
	private Colour last;
	private long currentTime;
	private Colour current;

	public AnimatedColourController(Controller<Colour> base) {
		this(base, 200);
	}

	@Override
	public Colour get(Component component, Colour defaultValue) {
		Colour baseValue = base.get(component, defaultValue);
		if (!baseValue.equals(current)) {
			last = current;
			current = baseValue;
			currentTime = System.currentTimeMillis();
		}

		return animate(Math.max(0, (System.currentTimeMillis() - currentTime) / ((float) duration)));
	}

	public Colour animate(float progress) {
		if (last == null || progress == 1)
			return current;

		return last.lerp(current, progress);
	}

}
