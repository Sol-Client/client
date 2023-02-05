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

package io.github.solclient.client.mod.keybinding;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.mod.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

public abstract class ToggledKeyBinding<ModType extends Mod> extends KeyBinding {

	public final ModType mod;
	private final MinecraftClient mc = MinecraftClient.getInstance();
	private boolean wasDown;
	private long startTime;

	public ToggledKeyBinding(ModType mod, String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.mod = mod;
	}

	@EventHandler
	public void tickBinding(PostTickEvent event) {
		boolean down = super.isPressed();
		if (mod.isEnabled()) {
			if (down) {
				if (!wasDown) {
					startTime = System.currentTimeMillis();
					if (getState() == ToggleState.TOGGLED) {
						postStateUpdate(ToggleState.HELD);
					} else {
						postStateUpdate(ToggleState.TOGGLED);
					}
				} else if ((System.currentTimeMillis() - startTime) > 250) {
					postStateUpdate(ToggleState.HELD);
				}
			} else if (getState() == ToggleState.HELD) {
				postStateUpdate(null);
			}

			wasDown = down;
		}
	}

	@Override
	public boolean isPressed() {
		if (mod.isEnabled())
			return mc.currentScreen == null && getState() != null;

		return super.isPressed();
	}

	public String getText(boolean editMode) {
		String translationId;
		if (editMode)
			translationId = String.format("sol_client.mod.%s.%s", mod.getId(),
					ToggleState.TOGGLED.name().toLowerCase());
		else
			translationId = String.format("sol_client.mod.%s.%s", mod.getId(), getState().name().toLowerCase());
		return I18n.translate(translationId);
	}

	public abstract void postStateUpdate(ToggleState newState);

	public abstract ToggleState getState();

}
