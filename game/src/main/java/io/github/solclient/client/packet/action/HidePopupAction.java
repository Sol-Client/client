package io.github.solclient.client.packet.action;

import java.util.UUID;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.packet.PacketApi;

public final class HidePopupAction implements ApiAction {

	@Expose
	private UUID uuid;

	@Override
	public void exec(PacketApi api) {
		if (!Client.INSTANCE.getPopupManager().remove(uuid) && api.isDevMode())
			PacketApi.LOGGER.warn("Tried to remove popup which wasn't present: {}", uuid);
	}

}
