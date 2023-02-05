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

package io.github.solclient.client.mod.impl.replay.fix;

import java.util.*;

import com.replaymod.lib.de.johni0702.minecraft.gui.utils.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.mod.impl.replay.SCReplayMod;

public class SCEventRegistrations {

	private final List<EventRegistration<?>> registrations = new ArrayList<>();
	private static final EventBus BUS = Client.INSTANCE.getEvents();

	public <T> SCEventRegistrations on(EventRegistration<T> registration) {
		registrations.add(registration);
		return this;
	}

	public <T> SCEventRegistrations on(Event<T> event, T listener) {
		return on(EventRegistration.create(event, listener));
	}

	public void register() {
		BUS.register(this);
		SCReplayMod.instance.addEvent(this);
		registrations.forEach(EventRegistration::register);
	}

	public void unregister() {
		BUS.unregister(this);
		SCReplayMod.instance.removeEvent(this);
		registrations.forEach(EventRegistration::unregister);
	}

}
