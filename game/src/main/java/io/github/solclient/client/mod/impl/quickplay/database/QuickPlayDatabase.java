package io.github.solclient.client.mod.impl.quickplay.database;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.github.solclient.client.util.Utils;
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
			JsonArray array = JsonParser
					.parseString(Utils.urlToString(Utils.sneakyParse("https://bugg.co/quickplay/mod/gamelist")))
					.getAsJsonObject().get("content").getAsJsonObject().get("games").getAsJsonArray();

			for(JsonElement gameElement : array) {
				games.put(gameElement.getAsJsonObject().get("unlocalizedName").getAsString(), new QuickPlayGame(gameElement.getAsJsonObject()));
			}
		}
		catch(IOException | JsonSyntaxException error) {
			throw new IllegalStateException(error);
		}
	}

}
