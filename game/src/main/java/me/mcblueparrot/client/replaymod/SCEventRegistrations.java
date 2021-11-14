/*
 * Includes modified decompiled Replay Mod class files.
 *
 * License for Replay Mod:
 *
 *     Copyright (C) <year>  <name of author>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.mcblueparrot.client.replaymod;

import com.replaymod.lib.de.johni0702.minecraft.gui.utils.Event;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistration;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.EventBus;
import me.mcblueparrot.client.mod.impl.SCReplayMod;

import java.util.ArrayList;
import java.util.List;

public class SCEventRegistrations {

    private List<EventRegistration<?>> registrations = new ArrayList<>();
    private static final EventBus BUS = Client.INSTANCE.bus;

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
