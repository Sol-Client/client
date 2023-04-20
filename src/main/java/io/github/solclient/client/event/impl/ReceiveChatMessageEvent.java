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

package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

@RequiredArgsConstructor
public class ReceiveChatMessageEvent {

	public final boolean actionBar;
	public final String originalMessage;
    public final Text formattedMessage;
	/**
	 * Whether the event is fired from the replay mod.
	 */
	public final boolean replay;
	public boolean cancelled;
    public Text newMessage = null;

}
