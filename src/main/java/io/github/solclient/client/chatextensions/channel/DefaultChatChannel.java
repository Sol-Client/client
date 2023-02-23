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

import java.util.Objects;

import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class DefaultChatChannel implements ChatChannel {

	private String name;
	private String command;

	public DefaultChatChannel(String name, String command) {
		this.name = name;
		this.command = command;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void sendMessage(ClientPlayerEntity player, String message) {
		if (command == null) {
			player.networkHandler.sendPacket(new ChatMessageC2SPacket(message));
		} else {
			player.sendChatMessage("/" + command + " " + message);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(command);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		DefaultChatChannel other = (DefaultChatChannel) obj;
		return Objects.equals(command, other.command);
	}

}