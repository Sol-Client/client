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

package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.Perspective;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.option.KeyBinding;

public class TaplookMod extends SolClientMod {

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey("key"), 0, GlobalConstants.KEY_CATEGORY);
	private int previousPerspective;
	private boolean active;
	@Expose
	@Option
	private Perspective perspective = Perspective.THIRD_PERSON_BACK;

	@Override
	public String getId() {
		return "taplook";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (key.isPressed()) {
			if (!active) {
				start();
			}
		} else if (active) {
			stop();
		}
	}

	public void start() {
		active = true;
		previousPerspective = mc.options.perspective;
		mc.options.perspective = perspective.ordinal();
		mc.worldRenderer.scheduleTerrainUpdate();
	}

	public void stop() {
		active = false;
		mc.options.perspective = previousPerspective;
		mc.worldRenderer.scheduleTerrainUpdate();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
