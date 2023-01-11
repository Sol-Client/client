package io.github.solclient.client.chatextensions.channel;

import net.minecraft.client.entity.EntityPlayerSP;

public interface ChatChannel {

	public String getName();

	public void sendMessage(EntityPlayerSP player, String message);

}