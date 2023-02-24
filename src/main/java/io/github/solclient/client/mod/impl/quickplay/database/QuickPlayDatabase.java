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

package io.github.solclient.client.mod.impl.quickplay.database;

import java.io.IOException;
import java.util.*;

import com.google.gson.*;

import io.github.solclient.util.Utils;
import lombok.Getter;

// Credit to original QuickPlay for database.
public class QuickPlayDatabase {

	@Getter
	private final Map<String, QuickPlayGame> games = new LinkedHashMap<>();

	public QuickPlayDatabase() {
		initDatabase();
	}

	public QuickPlayGame getGame(String id) {
		return games.get(id);
	}

	private void initDatabase() {
		try {
			JsonArray array = new JsonParser()
					.parse(Utils.urlToString(Utils.sneakyParse("https://bugg.co/quickplay/mod/gamelist")))
					.getAsJsonObject().get("content").getAsJsonObject().get("games").getAsJsonArray();

			for (JsonElement gameElement : array) {
				games.put(gameElement.getAsJsonObject().get("unlocalizedName").getAsString(),
						new QuickPlayGame(gameElement.getAsJsonObject()));
			}
		} catch (IOException | JsonSyntaxException error) {
			throw new IllegalStateException(error);
		}
	}

}
