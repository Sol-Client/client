package io.github.solclient.client.packet;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.*;

import com.google.gson.*;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.ServerConnectEvent;
import io.github.solclient.client.packet.action.ApiAction;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

// https://gist.github.com/TheKodeToad/16550462d8c323c4a7ecd60d0dd3ce8f
public final class PacketApi {

	public static final Logger LOGGER = LogManager.getLogger();
	private static final String CHANNEL = "sol-client:v1";

	private final Minecraft mc = Minecraft.getMinecraft();
	@Getter
	private boolean devMode;

	private void send(String channel, String data) {
		mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(channel,
				new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes(StandardCharsets.UTF_8)))));
	}

	@SuppressWarnings("unused")
	private void send(String data) {
		send(CHANNEL, data);
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		devMode = false;
	}

	@EventHandler
	public void receive(S3FPacketCustomPayload payload) {
		// as far as I know, no servers adopted the old api
		// this is why there is no backwards-compatibility
		if (payload.getChannelName() == null)
			return;

		if (payload.getChannelName().equals("REGISTER")) {
			register(payload);
			return;
		}

		if (!payload.getChannelName().equals(CHANNEL))
			return;

		try {
			receive(payload.getBufferData().toString(StandardCharsets.UTF_8));
		} catch (ApiUsageError error) {
			LOGGER.warn("A Sol Client API usage error has occured. This is most likely an issue with the server.",
					error);
		} catch (Throwable error) {
			LOGGER.error("An error occured while processing packet.", error);
		}
	}

	private void receive(String data) {
		receive(JsonParser.parseString(data).getAsJsonObject());
	}

	private void receive(JsonObject object) {
		if (!object.has("action"))
			throw new ApiUsageError("Missing action in payload");
		if (!isString(object.get("action")))
			throw new ApiUsageError("API version is not a string");

		if (!object.has("inputs"))
			throw new ApiUsageError("Missing inputs in payload");
		if (!object.get("inputs").isJsonObject())
			throw new ApiUsageError("Inputs is not an object");

		ApiAction action = ApiAction.createAction(object.get("action").getAsString(),
				object.get("inputs").getAsJsonObject());
		action.exec(this);
	}

	private static boolean isString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	private void register(S3FPacketCustomPayload payload) {
		String[] channels = payload.getBufferData().toString(StandardCharsets.UTF_8).split("\0");

		for (String channel : channels)
			if (channel.equals(CHANNEL))
				send("REGISTER", channel);
	}

	public void enableDevMode() {
		devMode = true;
	}

}
