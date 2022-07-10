package io.github.solclient.client.chat;

import io.github.solclient.client.platform.mc.MinecraftClient;

public interface ChatButton {

	int getPriority();

	default int getWidth() {
		return MinecraftClient.getInstance().getFont().getWidth(getText()) + 4;
	}

	int getPopupWidth();

	int getPopupHeight();

	String getText();

	void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY);

	default boolean isOpen() {
		return MinecraftClient.getInstance().getScreen(ChatAccessor.class)
				.map((chat) -> chat.getSelectedButton() == this).orElse(false);
	}

}
