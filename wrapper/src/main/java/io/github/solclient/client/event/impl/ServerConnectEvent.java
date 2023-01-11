package io.github.solclient.client.event.impl;

import io.github.solclient.client.DetectedServer;
import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.ServerData;

@AllArgsConstructor
public class ServerConnectEvent {

	public final ServerData data;
	public final DetectedServer server;

}
