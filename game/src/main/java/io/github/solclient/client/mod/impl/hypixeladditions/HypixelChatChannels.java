package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.*;

import io.github.solclient.client.ChatChannelSystem;
import io.github.solclient.client.ChatChannelSystem.ChatChannel.DefaultChatChannel;

public class HypixelChatChannels extends ChatChannelSystem {

	public static final ChatChannel PARTY = new DefaultChatChannel("Party", "pchat");
	public static final ChatChannel GUILD = new DefaultChatChannel("Guild", "gchat");
	public static final ChatChannel OFFICER = new DefaultChatChannel("Guild Officer", "ochat");
	public static final ChatChannel COOP = new DefaultChatChannel("Skyblock Co-op", "coopchat");

	private static final List<ChatChannel> CHANNELS = Arrays.asList(ALL, PARTY, GUILD, OFFICER, COOP);

	@Override
	public List<ChatChannel> getChannels() {
		return CHANNELS;
	}

}
