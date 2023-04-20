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

package io.github.solclient.client.mod.impl.tweaks;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;
import net.java.games.input.Mouse;

@RequiredArgsConstructor
public final class RawInput implements Runnable {

	private final TweaksMod mod;

	private volatile Thread thread;
	private boolean initialised;
	@Getter
	private boolean available;
	private volatile boolean running;
	private final List<Mouse> mice = new ArrayList<>();
	@Getter
	private volatile float dx, dy;

	private void init() {
		if (initialised)
			return;

		initialised = available = true;

		try {
			ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();

			if (env.isSupported()) {
				for (Controller controller : env.getControllers())
					if (controller instanceof Mouse)
						mice.add((Mouse) controller);

				// not working :/
				// i don't think this is even implemented!
				env.addControllerListener(new ControllerListener() {

					@Override
					public void controllerRemoved(ControllerEvent event) {
						if (event.getController() instanceof Mouse)
							mice.remove(event.getController());
					}

					@Override
					public void controllerAdded(ControllerEvent event) {
						if (event.getController() instanceof Mouse)
							mice.add((Mouse) event.getController());
					}

				});

				return;
			} else
				mod.getLogger().warn("Controller environment is not supported");
		} catch (Throwable error) {
			mod.getLogger().error("Failed to initialise controller environment", error);
		}

		available = false;
	}

	public void start() {
		init();

		if (thread != null) {
			thread.interrupt();
			thread = null;
		}

		running = true;
		thread = new Thread(this, "Mouse input");
		thread.setDaemon(true);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		while (running) {
			available = !mice.isEmpty();

			for (Mouse mouse : mice) {
				if (!mouse.poll())
					continue;

				float dx = mouse.getX().getPollData();
				float dy = mouse.getY().getPollData();

				if (org.lwjgl.input.Mouse.isGrabbed()) {
					this.dx += dx;
					this.dy += dy;
				}
			}
		}
	}

	public void reset() {
		dx = dy = 0;
	}

}
