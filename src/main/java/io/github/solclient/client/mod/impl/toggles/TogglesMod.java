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

package io.github.solclient.client.mod.impl.toggles;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.resource.language.I18n;

public class TogglesMod extends SolClientSimpleHudMod {

	ToggleState state;
	@Expose
	@Option
	private boolean hud;
	@Expose
	@Option
	private boolean toggleSprint = true;
	@Expose
	@Option
	private boolean toggleSneak;

	private ToggleKeyBinding sprint;
	private ToggleKeyBinding sneak;

	@Override
	public void init() {
		super.init();

		MinecraftUtils.unregisterKeyBinding(mc.options.sprintKey);
		sprint = new ToggleKeyBinding(() -> isEnabled() && toggleSprint, mc.options.sprintKey.getTranslationKey(),
				Keyboard.KEY_LCONTROL, mc.options.sprintKey.getCategory());
		mc.options.sprintKey = sprint;
		MinecraftUtils.registerKeyBinding(sprint);

		MinecraftUtils.unregisterKeyBinding(mc.options.sneakKey);
		sneak = new ToggleKeyBinding(() -> isEnabled() && toggleSneak, mc.options.sneakKey.getTranslationKey(),
				Keyboard.KEY_LSHIFT, mc.options.sneakKey.getCategory());
		mc.options.sneakKey = sneak;
		MinecraftUtils.registerKeyBinding(sneak);
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
		if (!hud)
			return null;
		if (editMode)
			return I18n.translate(getTranslationKey("sprint_toggled"));

		// sneak takes precedence as it should be impossible to take both actions at
		// once!
		if (sneak.getState() != null)
			return I18n.translate(getTranslationKey("sneak_" + sneak.getState().name().toLowerCase()));
		if (sprint.getState() != null)
			return I18n.translate(getTranslationKey("sprint_" + sprint.getState().name().toLowerCase()));

		return null;
	}

}
