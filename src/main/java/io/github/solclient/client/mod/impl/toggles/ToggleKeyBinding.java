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

import com.google.common.base.Supplier;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class ToggleKeyBinding extends KeyBinding {

	@Getter
	private ToggleState state;
	private final Supplier<Boolean> controller;
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	private boolean wasDown;
	private long startTime;

	public ToggleKeyBinding(Supplier<Boolean> controller, String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.controller = controller;

		Client.INSTANCE.getEvents().register(this);
	}

	@EventHandler
	public void onTickBinding(PostTickEvent event) {
		tickBinding();
	}

	protected void tickBinding() {
		boolean down = isPhysicallyPressed();
		if (!controller.get())
			return;

		if (down) {
			if (!wasDown) {
				startTime = System.currentTimeMillis();
				if (state == ToggleState.TOGGLED)
					state = ToggleState.HELD;
				else
					state = ToggleState.TOGGLED;
			} else if ((System.currentTimeMillis() - startTime) > 250) {
				state = ToggleState.HELD;
			}
		} else if (state == ToggleState.HELD)
			state = null;

		wasDown = down;
	}

	protected boolean isPhysicallyPressed() {
		return super.isPressed();
	}

	@Override
	public boolean isPressed() {
		if (controller.get())
			return mc.currentScreen == null && state != null;

		return isPhysicallyPressed();
	}

}