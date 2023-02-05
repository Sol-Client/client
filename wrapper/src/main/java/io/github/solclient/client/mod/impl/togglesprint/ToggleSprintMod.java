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

package io.github.solclient.client.mod.impl.togglesprint;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.mod.keybinding.ToggleState;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;

public class ToggleSprintMod extends SolClientSimpleHudMod {

	private ToggleState sprint;
	@Expose
	@Option
	private boolean hud;

	private ToggleSprintKeyBinding keybinding;

	@Override
	public void init() {
		super.init();

		MinecraftUtils.unregisterKeyBinding(mc.options.sprintKey);
		keybinding = new ToggleSprintKeyBinding(this, mc.options.sprintKey.getTranslationKey(), 29,
				mc.options.sprintKey.getCategory());
		mc.options.sprintKey = keybinding;
		MinecraftUtils.registerKeyBinding(keybinding);
	}

	@Override
	public String getId() {
		return "toggle_sprint";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public boolean isVisible() {
		return hud;
	}

	@Override
	public String getText(boolean editMode) {
		if (!hud) {
			return null;
		}
		if (editMode) {
			return keybinding.getText(true);
		}
		return getSprint() == null ? null : keybinding.getText(false);
	}

	public ToggleState getSprint() {
		return sprint;
	}

	public void setSprint(ToggleState sprint) {
		this.sprint = sprint;
	}

}
