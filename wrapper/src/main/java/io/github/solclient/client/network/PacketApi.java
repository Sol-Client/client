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

package io.github.solclient.client.network;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.*;

import com.google.gson.*;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.ServerConnectEvent;
import io.github.solclient.client.network.action.ApiAction;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.PacketByteBuf;

// https://gist.github.com/TheKodeToad/16550462d8c323c4a7ecd60d0dd3ce8f
public final class PacketApi {

	public static final Logger LOGGER = LogManager.getLogger();
	private static final String CHANNEL = "sol-client:v1";

	private final MinecraftClient mc = MinecraftClient.getInstance();
	@Getter
	private boolean devMode;

	private void send(String channel, String data) {
		mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(channel,
				new PacketByteBuf(Unpooled.wrappedBuffer(data.getBytes(StandardCharsets.UTF_8)))));
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
	public void receive(CustomPayloadS2CPacket payload) {
		// as far as I know, no servers adopted the old api
		// this is why there is no backwards-compatibility
		if (payload.getChannel() == null)
			return;

		if (payload.getChannel().equals("REGISTER")) {
			register(payload);
			return;
		}

		if (!payload.getChannel().equals(CHANNEL))
			return;

		try {
			receive(payload.getPayload().toString(StandardCharsets.UTF_8));
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

	private void register(CustomPayloadS2CPacket payload) {
		String[] channels = payload.getPayload().toString(StandardCharsets.UTF_8).split("\0");

		for (String channel : channels)
			if (channel.equals(CHANNEL))
				send("REGISTER", channel);
	}

	public void enableDevMode() {
		devMode = true;
	}

}
