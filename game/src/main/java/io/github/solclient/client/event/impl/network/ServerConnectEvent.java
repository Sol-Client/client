package io.github.solclient.client.event.impl.network;

import io.github.solclient.client.DetectedServer;
import io.github.solclient.client.platform.mc.network.ServerData;
import lombok.*;

@Data
@RequiredArgsConstructor
public class ServerConnectEvent {

	private final ServerData data;
	private final DetectedServer detected;

}
