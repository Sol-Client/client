package io.github.solclient.client.event.impl.network;

import org.jetbrains.annotations.Nullable;

import com.google.gson.*;

import io.github.solclient.client.platform.mc.resource.Identifier;
import lombok.*;

/**
 * Fired when the server sends a plugin message.
 */
@Data
@AllArgsConstructor
public final class ServerMessageReceiveEvent {

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
	 * Guaranteed to be not null if <code>EnvironmentConstants.PROPER_PLUGIN_MESSAGE_IDS == true</code>.
	 * @return The channel if it can be resolved as an id, or else <code>null<code>.
	 */
	public @Nullable Identifier getChannelId() {
		if(channelId != null) {
			return channelId;
		}

		try {
			return channelId = Identifier.parse(channel);
		}
		catch(Throwable error) {
			return null;
		}
	}

}
