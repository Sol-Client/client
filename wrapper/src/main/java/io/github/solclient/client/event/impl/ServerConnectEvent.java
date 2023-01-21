package io.github.solclient.client.event.impl;

import io.github.solclient.client.DetectedServer;
import lombok.AllArgsConstructor;
import net.minecraft.client.network.ServerInfo;

@AllArgsConstructor
public class ServerConnectEvent {

	public final ServerInfo info;
	public final DetectedServer server;

}
