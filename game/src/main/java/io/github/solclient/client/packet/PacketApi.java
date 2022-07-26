package io.github.solclient.client.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.network.ServerMessageReceiveEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.platform.mc.resource.Identifier;

public class PacketApi {

	private static final Logger LOGGER = LogManager.getLogger();

	@EventHandler
	public void onServerMessage(ServerMessageReceiveEvent payload) {
		Identifier id = payload.getChannelId();
		if(id != null && (id.namespace().equals("sol_client") || id.namespace().equals(/* deprecated */ "solclient"))) {
			JsonElement message = payload.getJson();

			if(id.path().equals("block_mods")) {
				JsonArray array = message.getAsJsonArray();

				Client.INSTANCE.getMods().forEach(Mod::unblock);

				for(JsonElement element : array) {
					String modId = element.getAsString();

					Mod mod = Client.INSTANCE.getModById(modId);

					if(mod != null) {
						mod.block();
					}
					else {
						LOGGER.warn("Cannot block mod " + modId + ": not found");
					}
				}
			}
			else if(id.path().equals("popup")) {
				JsonObject data = message.getAsJsonObject();

				String text = data.get("text").getAsString();
				String command = data.get("command").getAsString();

				Client.INSTANCE.getPopupManager().add(new Popup(text, command));
			}
		}
	}

}
