package io.github.solclient.client.chatextensions;

import io.github.solclient.client.extension.ChatScreenExtension;
import net.minecraft.client.MinecraftClient;

public interface ChatButton {

	int getPriority();

	default int getWidth() {
		return MinecraftClient.getInstance().textRenderer.getStringWidth(getText()) + 4;
	}

	int getPopupWidth();

	int getPopupHeight();

	String getText();

	void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY);

	default boolean isOpen() {
		ChatScreenExtension chat = ChatScreenExtension.active();
		return chat != null && chat.getSelectedChatButton() == this;
	}

}
