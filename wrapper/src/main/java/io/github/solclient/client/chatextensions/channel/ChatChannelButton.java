package io.github.solclient.client.chatextensions.channel;

import io.github.solclient.client.Client;
import io.github.solclient.client.chatextensions.ChatButton;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.client.util.extension.ChatScreenExtension;
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
								.getStringWidth(Client.INSTANCE.getChatExtensions().getChannelSystem().getChannels()
										.stream().map(ChatChannel::getName).max(Utils.STRING_WIDTH_COMPARATOR).get())
								+ 2);
	}

	@Override
	public int getPopupHeight() {
		return (Client.INSTANCE.getChatExtensions().getChannelSystem().getChannels().size() * 13) - 1;
	}

	@Override
	public String getText() {
		return Client.INSTANCE.getChatExtensions().getChannelSystem().getChannelName() + (isOpen() ? " ▼" : " ▲");
	}

	@Override
	public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX,
			int mouseY) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;

		for (ChatChannel channel : Client.INSTANCE.getChatExtensions().getChannelSystem().getChannels()) {
			Rectangle optionBounds = new Rectangle(x, y, getPopupWidth(), 12);
			boolean hovered = optionBounds.contains(mouseX, mouseY);
			optionBounds.fill(hovered ? Colour.WHITE_128 : Colour.BLACK_128);
			if (hovered && wasMouseClicked) {
				Utils.playClickSound(false);
				ChatScreenExtension.active().setSelectedChatButton(null);
				Client.INSTANCE.getChatExtensions().getChannelSystem().setChannel(channel);
			}

			font.draw(channel.getName(),
					optionBounds.getX() + (optionBounds.getWidth() / 2) - (font.getStringWidth(channel.getName()) / 2),
					optionBounds.getY() + (optionBounds.getHeight() / 2) - (font.fontHeight / 2), hovered ? 0 : -1);
			y += 13;
		}
	}

}