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

package io.github.solclient.client.mod.impl.api.chat;

import java.util.*;

import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.impl.api.chat.channel.ChatChannelSystem;
import lombok.*;

public final class ChatApiMod extends StandardMod {

	public static ChatApiMod instance;

	@Getter
	@Setter
	private ChatChannelSystem channelSystem;

	private boolean sortingDirty;
	private final List<ChatButton> buttons = new LinkedList<>();

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	public void registerButton(ChatButton button) {
		buttons.add(button);
		// we could use insersion sort... but me be very lazy today zzzz
		sortingDirty = true;
	}

	public void unregisterButton(ChatButton button) {
		buttons.remove(button);
	}

	public List<ChatButton> getButtons() {
		if (sortingDirty) {
			sortingDirty = false;
			buttons.sort(Comparator.comparingInt(ChatButton::getPriority));
		}

		return buttons;
	}

}
