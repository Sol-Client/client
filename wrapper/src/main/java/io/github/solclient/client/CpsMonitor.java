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

package io.github.solclient.client;

import java.util.*;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;

public class CpsMonitor {

	public static final CpsMonitor LMB = new CpsMonitor(0);
	public static final CpsMonitor RMB = new CpsMonitor(1);

	public static void forceInit() {
	}

	private int button;
	private List<Long> presses = new ArrayList<Long>();

	public CpsMonitor(int button) {
		this.button = button;
		Client.INSTANCE.getEvents().register(this);
	}

	@EventHandler
	public void onMouseClickEvent(MouseClickEvent event) {
		if (event.button == button) {
			click();
		}
	}

	public void click() {
		presses.add(System.currentTimeMillis());
	}

	@EventHandler
	public void tick(PostTickEvent event) {
		presses.removeIf(t -> System.currentTimeMillis() - t > 1000);
	}

	public int getCps() {
		return presses.size();
	}

}
