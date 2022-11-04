package io.github.solclient.client.chat;

import io.github.solclient.client.platform.mc.network.C2SChatMessagePacket;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
import lombok.Data;

@Data
public class DefaultChatChannel implements ChatChannel {

	private final String name, command;

	@Override
	public void sendMessage(LocalPlayer player, String message) {
		if(command == null) {
			player.getConnection().sendPacket(C2SChatMessagePacket.create(message));
		}
		else {
			player.chat("/" + command + " " + message);
		}
	}

}