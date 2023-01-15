package io.github.solclient.client.chatextensions.channel;

import net.minecraft.entity.player.ClientPlayerEntity;

public interface ChatChannel {

	public String getName();

	public void sendMessage(ClientPlayerEntity player, String message);

}