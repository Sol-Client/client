package io.github.solclient.client.packet.action;

import io.github.solclient.client.packet.PacketApi;

public class EnableDevModeAction implements ApiAction {

	@Override
	public void exec(PacketApi api) {
		api.enableDevMode();
	}

}
