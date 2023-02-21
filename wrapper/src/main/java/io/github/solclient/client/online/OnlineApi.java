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

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;

import org.apache.logging.log4j.*;

import com.google.common.base.Objects;
import com.google.gson.*;

import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.util.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OnlineApi {

	private Map<UUID, Boolean> cache;
	private final Logger LOGGER = LogManager.getLogger();

	static {
		clearCache();
	}

	/**
	 * Fetch and cache a stream of uuids.
	 *
	 * @param uuids the uuids.
	 */
	public void fetch(Stream<UUID> uuids) {
		List<UUID> filtered;
		synchronized (cache) {
			filtered = uuids.filter(((Predicate<UUID>) cache::containsKey).negate()).collect(Collectors.toList());
			if (filtered.isEmpty())
				return;

			for (UUID uuid : filtered)
				cache.put(uuid, null);
		}

		MinecraftUtils.USER_DATA.submit(() -> {
			try {
				String param = join(filtered);
				try (Reader reader = new InputStreamReader(Utils.getConnection(GlobalConstants.USER_AGENT,
						new URL(GlobalConstants.API + "/online/get/" + param)).getInputStream())) {
					int index = 0;
					for (JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
						if (index >= filtered.size())
							break;

						synchronized (cache) {
							cache.put(filtered.get(index),
									element.isJsonPrimitive() && Duration
											.between(ZonedDateTime.parse(element.getAsString()), ZonedDateTime.now())
											.toMinutes() < 30);
						}
						index++;
					}
				}
			} catch (IOException error) {
				LOGGER.error("Failed to get player data", error);
			}
		});
	}

	/**
	 * Recall a cached uuid.
	 *
	 * @param uuid the uuid.
	 * @return whether the player is online.
	 */
	public boolean recall(UUID uuid) {
		return Objects.firstNonNull(cache.get(uuid), false);
	}

	/**
	 * Recall a cached uuid, otherwise fetch it.
	 *
	 * @param uuid the uuid.
	 * @return whether the player is online.
	 */
	public boolean recallOrFetch(UUID uuid) {
		if (uuid.version() != 4)
			return false;

		fetch(Stream.of(uuid));
		return recall(uuid);
	}

	private String join(List<UUID> players) {
		StringBuilder result = new StringBuilder();
		for (int index = 0; index < players.size(); index++) {
			if (index != 0)
				result.append(',');
			result.append(players.get(index));
		}
		return result.toString();
	}

	public void logIn(UUID uuid) throws IOException {
		new URL(GlobalConstants.API + "/online/log_in/" + uuid.toString()).openConnection().getInputStream();
	}

	public void logOut(UUID uuid) throws IOException {
		new URL(GlobalConstants.API + "/online/log_out/" + uuid.toString()).openConnection().getInputStream();
	}

	public void clearCache() {
		cache = new HashMap<>();
		cache.put(MinecraftUtils.getPlayerUuid(), true);
	}

}
