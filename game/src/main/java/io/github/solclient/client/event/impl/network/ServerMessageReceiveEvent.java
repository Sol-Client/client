package io.github.solclient.client.event.impl.network;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.solclient.api.Identifier;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Fired when the server sends a plugin message.
 */
@Data
@AllArgsConstructor
public class ServerMessageReceiveEvent {

	private Identifier channelId;
	private final String channel;
	private final String data;

	public JsonElement getJson() {
		return JsonParser.parseString(data);
	}

	public JsonObject getJsonObject() {
		return getJson().getAsJsonObject();
	}

	public JsonArray getJsonArray() {
		return getJson().getAsJsonArray();
	}

	/**
	 * Returns the channel identifier.
	 * Guaranteed to be not null on newer versions of the game.
	 * @return The channel if it can be resolved as an id, or else <code>null<code>.
	 */
	public @Nullable Identifier getChannelId() {
		if(channelId != null) {
			return channelId;
		}

		try {
			return Identifier.parse(channel);
		}
		catch(Throwable error) {
			return null;
		}
	}

}
