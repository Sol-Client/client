package io.github.solclient.client.chat;

import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;

public interface ChatChannel {

	public String getName();

	public void sendMessage(LocalPlayer player, String message);

}