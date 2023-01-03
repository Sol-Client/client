package io.github.solclient.client.api;

import org.apache.logging.log4j.*;

import com.google.gson.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.mod.Mod;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class ClientApi {

	private static final Logger LOGGER = LogManager.getLogger();

	@EventHandler
	public void onCustomPayload(S3FPacketCustomPayload payload) {
		if (payload.getChannelName().startsWith("solclient:")) {
			String message = payload.getBufferData().readStringFromBuffer(32767);

			if (payload.getChannelName().equals("solclient:block_mods")) {
				JsonArray array = JsonParser.parseString(message).getAsJsonArray();

				Client.INSTANCE.getMods().forEach(Mod::unblock);

				for (JsonElement element : array) {
					String modId = element.getAsString();

					Mod mod = Client.INSTANCE.getModById(modId);

					if (mod != null) {
						mod.block();
					} else {
						LOGGER.warn("Cannot block mod " + modId + ": not found");
					}
				}
			} else if (payload.getChannelName().equals("solclient:popup")) {
				JsonObject data = JsonParser.parseString(message).getAsJsonObject();

				String text = data.get("text").getAsString();
				String command = data.get("command").getAsString();

				Client.INSTANCE.getPopupManager().add(new Popup(text, command));
			}
		}
	}

}
