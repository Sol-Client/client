package io.github.solclient.client.chat;

import io.github.solclient.client.Client;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.hud.chat.Chat;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

public class ChatChannelButton implements ChatButton {

	public static final ChatChannelButton INSTANCE = new ChatChannelButton();

	private ChatChannelButton() {}

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
		return Math.max(ChatButton.super.getWidth(), Utils.getStringWidth(Client.INSTANCE.getChatChannelSystem()
				.getChannels().stream().map(ChatChannel::getName).max(Utils.STRING_WIDTH_COMPARATOR).get()) + 2);
	}

	@Override
	public int getPopupHeight() {
		return (Client.INSTANCE.getChatChannelSystem().getChannels().size() * 13) - 1;
	}

	@Override
	public String getText() {
		return Client.INSTANCE.getChatChannelSystem().getChannelName() + (isOpen() ? " ▼" : " ▲");
	}

	@Override
	public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY) {
		Font font = MinecraftClient.getInstance().getFont();

		for(ChatChannel channel : Client.INSTANCE.getChatChannelSystem().getChannels()) {
			Rectangle optionBounds = new Rectangle(x, y, getPopupWidth(), 12);
			boolean hovered = optionBounds.contains(mouseX, mouseY);
			optionBounds.fill(hovered ? Colour.WHITE_128 : Colour.BLACK_128);
			if(hovered && wasMouseClicked) {
				Utils.playClickSound(false);
				Chat.requireInstance().setSelectedButton(null);
				Client.INSTANCE.getChatChannelSystem().setChannel(channel);
			}

			font.render(channel.getName(),
					optionBounds.getX() + (optionBounds.getWidth() / 2)
							- (font.getTextWidth(channel.getName()) / 2),
					optionBounds.getY() + (optionBounds.getHeight() / 2) - (font.getHeight() / 2), hovered ? 0 :
							-1);
			y += 13;
		}
	}

}