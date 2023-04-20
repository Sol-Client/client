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

package io.github.solclient.client.mod.impl.api.chat.channel;

import io.github.solclient.client.extension.ChatScreenExtension;
import io.github.solclient.client.mod.impl.api.chat.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class ChatChannelButton implements ChatButton {

	public static final ChatChannelButton INSTANCE = new ChatChannelButton();

	private ChatChannelButton() {
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getWidth() {
		return Math.max(isOpen() ? getPopupWidth() : 0, ChatButton.super.getWidth());
	}

	@Override
	public int getPopupWidth() {
		return Math
				.max(ChatButton.super.getWidth(),
						MinecraftClient.getInstance().textRenderer
								.getStringWidth(ChatApiMod.instance.getChannelSystem().getChannels().stream()
										.map(ChatChannel::getName).max(MinecraftUtils.STRING_WIDTH_COMPARATOR).get())
								+ 2);
	}

	@Override
	public int getPopupHeight() {
		return (ChatApiMod.instance.getChannelSystem().getChannels().size() * 13) - 1;
	}

	@Override
	public String getText() {
		return ChatApiMod.instance.getChannelSystem().getChannelName() + (isOpen() ? " ▼" : " ▲");
	}

	@Override
	public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX,
			int mouseY) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;

		for (ChatChannel channel : ChatApiMod.instance.getChannelSystem().getChannels()) {
			Rectangle optionBounds = new Rectangle(x, y, getPopupWidth(), 12);
			boolean hovered = optionBounds.contains(mouseX, mouseY);
			optionBounds.fill(hovered ? Colour.WHITE_128 : Colour.BLACK_128);
			if (hovered && wasMouseClicked) {
				MinecraftUtils.playClickSound(false);
				ChatScreenExtension.active().setSelectedChatButton(null);
				ChatApiMod.instance.getChannelSystem().setChannel(channel);
			}

			font.draw(channel.getName(),
					optionBounds.getX() + (optionBounds.getWidth() / 2) - (font.getStringWidth(channel.getName()) / 2),
					optionBounds.getY() + (optionBounds.getHeight() / 2) - (font.fontHeight / 2), hovered ? 0 : -1);
			y += 13;
		}
	}

}