/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.chatextensions.channel;

import java.util.List;

public abstract class ChatChannelSystem {

	public static final ChatChannel ALL = new DefaultChatChannel("All", null);

	private ChatChannel channel = ALL;

	public abstract List<ChatChannel> getChannels();

	public static ChatChannel getPrivateChannel(String player) {
		return new DefaultChatChannel(player, "msg " + player);
	}

	public ChatChannel getChannel() {
		return channel;
	}

	public String getChannelName() {
		return channel.getName();
	}

	public void setChannel(ChatChannel channel) {
		this.channel = channel;
	}

}
