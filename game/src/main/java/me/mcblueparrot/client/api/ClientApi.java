package me.mcblueparrot.client.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.mod.Mod;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class ClientApi {

	private static final Logger LOGGER = LogManager.getLogger();

	@EventHandler
	public void onCustomPayload(S3FPacketCustomPayload payload) {
		if(payload.getChannelName().equals("solclient:block_mods")) {
			String message = payload.getBufferData().readStringFromBuffer(32767);

			JsonArray array = new JsonParser().parse(message).getAsJsonArray();

			Client.INSTANCE.getMods().forEach(Mod::unblock);

			for(JsonElement element : array) {
				String modId = element.getAsString();

				System.out.println(modId);

				Mod mod = Client.INSTANCE.getModById(modId);

				if(mod != null) {
					mod.block();
				}
				else {
					LOGGER.warn("Cannot block mod " + modId + ": not found");
				}
			}
		}
	}

}
