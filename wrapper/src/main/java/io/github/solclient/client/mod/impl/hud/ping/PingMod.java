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

package io.github.solclient.client.mod.impl.hud.ping;

import java.net.UnknownHostException;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.network.PlayerListEntry;

public class PingMod extends SmoothCounterHudMod {

	private static final int PING_INTERVAL = 600;

	private int ping;
	private int nextPing;

	@Expose
	@Option
	private PingSource source = PingSource.AUTO;

	@Override
	public String getId() {
		return "ping";
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		ping = 0;
		nextPing = 60;
	}

	@EventHandler
	public void updatePing(PostTickEvent event) {
		if (source.resolve() != PingSource.MULTIPLAYER_SCREEN) {
			return;
		}

		if (mc.getCurrentServerEntry() != null && !mc.isIntegratedServerRunning()) {
			if (nextPing > 0) {
				nextPing--;
			} else if (nextPing > -1) {
				nextPing = -1;

				Thread thread = new Thread(() -> {
					try {
						MinecraftUtils.pingServer(mc.getCurrentServerEntry().address, (newPing) -> {
							if (newPing != -1) {
								if (ping != 0) {
									ping = (ping * 3 + newPing) / 4;
								} else {
									ping = newPing;
								}
							}
						});
					} catch (UnknownHostException error) {
						logger.error("Could not ping server", error);
					}

					nextPing = PING_INTERVAL;
				});
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	@Override
	public int getIntValue() {
		if (ping != -1 && source.resolve() == PingSource.MULTIPLAYER_SCREEN) {
			return ping;
		}

		if (source.resolve() == PingSource.TAB_LIST && !mc.isIntegratedServerRunning() && mc.player != null
				&& mc.getCurrentServerEntry() != null) {
			PlayerListEntry entry = mc.player.networkHandler.getPlayerListEntry(mc.getSession().getProfile().getId());
			if (entry != null)
				return entry.getLatency();
		}

		return 0;
	}

	@Override
	public String getSuffix() {
		return "ms";
	}

}
