package io.github.solclient.client;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.world.entity.player.Player;
import io.github.solclient.client.util.Utils;

public class CapeManager {

	private static final Logger LOGGER = LogManager.getLogger();
	private Map<String, String> capes = new HashMap<>();
	private Map<UUID, Identifier> capeCache = new HashMap<>();
	private static final String BASE_URL = "https://raw.githubusercontent.com/Sol-Client/Capes/main/";
	private static final URL BY_PLAYER_URL = Utils.sneakyParse(BASE_URL + "by_player.json");

	public CapeManager() {
		Utils.MAIN_EXECUTOR.submit(() -> {
			try {
				JsonObject capesObject = JsonParser.parseReader(new InputStreamReader(BY_PLAYER_URL.openStream()))
						.getAsJsonObject();

				for(Map.Entry<String, JsonElement> entry : capesObject.entrySet()) {
					capes.put(entry.getKey(), BASE_URL + "capes/" + entry.getValue().getAsString() + ".png");
				}
			}
			catch(Exception error) {
				LOGGER.error("Could not load capes", error);
			}
		});
	}

	public Identifier getForPlayer(Player player) {
		MinecraftClient mc = MinecraftClient.getInstance();

		String capeUrl = capes.get(player.getId().toString().replace("-", ""));

		if(capeUrl == null) {
			return null;
		}

		try {
			if(capeCache.containsKey(player.getId())) {
				return capeCache.get(player.getId());
			}
			else {
				Identifier cape = Identifier.solClient("capes/" + FilenameUtils.getBaseName(capeUrl));

				if(mc.getTextureManager().getTexture(cape) == null) {
					mc.getTextureManager().download(capeUrl, cape);
				}

				capeCache.put(player.getId(), cape);

				return cape;
			}
		}
		catch(Exception error) {
			LOGGER.error("Could not download cape", error);
		}

		return null;
	}

}
