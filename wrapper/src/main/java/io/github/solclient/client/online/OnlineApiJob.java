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

package io.github.solclient.client.online;

import java.io.IOException;
import java.time.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.MinecraftUtils;

public final class OnlineApiJob {

	private ZonedDateTime lastUpdate;

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		OnlineApi.clearCache();
	}

	@EventHandler
	public void onTick(PreTickEvent event) throws IOException {
		if (Duration.between(lastUpdate, ZonedDateTime.now()).toMinutes() >= 29) {
			lastUpdate = ZonedDateTime.now();
			OnlineApi.logIn(MinecraftUtils.getPlayerUuid());
		}
	}

	@EventHandler
	public void onPostStart(PostGameStartEvent event) {
		try {
			OnlineApi.logIn(MinecraftUtils.getPlayerUuid());
			lastUpdate = ZonedDateTime.now();
		} catch (IOException error) {
			Client.INSTANCE.getEvents().unregister(this);
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) throws IOException {
		OnlineApi.logOut(MinecraftUtils.getPlayerUuid());
	}

}
