package io.github.solclient.client.event.impl.network;

import io.github.solclient.abstraction.mc.network.ServerData;
import io.github.solclient.client.DetectedServer;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ServerConnectEvent {

	private final ServerData data;
	private final DetectedServer detected;

}
