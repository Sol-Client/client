package io.github.solclient.client.packet.action;

import java.util.UUID;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.packet.*;

public final class ShowPopupAction implements ApiAction {

	@Expose
	private UUID handle;
	@Expose
	private String text, command;
	@Expose
	private int time = 10000;

	@Override
	public void exec(PacketApi api) {
		if (text == null)
			throw new ApiUsageError("No text provided");
		if (command == null)
			throw new ApiUsageError("No command provided");

		Client.INSTANCE.getPopupManager().add(new Popup(text, command, time), handle);
	}

}
