package io.github.solclient.client;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.logging.log4j.*;

import com.google.gson.*;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.texture.Texture;
import io.github.solclient.client.platform.mc.world.entity.player.Player;
import io.github.solclient.client.util.Utils;

public class CapeManager {

	private static final Logger LOGGER = LogManager.getLogger();
	private Map<String, String> capes = new HashMap<>();
	private Map<UUID, Texture> capeCache = new HashMap<>();
	private static final String BASE_URL = "https://raw.githubusercontent.com/Sol-Client/Capes/main/";
	private static final URL BY_PLAYER_URL = Utils.sneakyParse(BASE_URL + "by_player.json");

	public CapeManager() {
		Utils.MAIN_EXECUTOR.submit(() -> {
			try {
				try(InputStream in = BY_PLAYER_URL.openStream()) {
					JsonObject capesObject = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();

					for(Map.Entry<String, JsonElement> entry : capesObject.entrySet()) {
						capes.put(entry.getKey(), BASE_URL + "capes/" + entry.getValue().getAsString() + ".png");
					}
				}
			}
			catch(Exception error) {
				LOGGER.error("Could not load capes", error);
			}
		});
	}

	public Texture getForPlayer(Player player) {
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
				mc.getTextureManager().download(capeUrl).thenAccept((texure) -> {
					mc.runSync(() -> {
						capeCache.put(player.getId(), texure);
					});
				});

				return null;
			}
		}
		catch(Exception error) {
			LOGGER.error("Could not download cape", error);
		}

		return null;
	}

}
