package io.github.solclient.client.extension;

import io.github.solclient.client.chatextensions.ChatButton;
import io.github.solclient.client.util.MinecraftUtils;

public interface ChatScreenExtension {

	void type(char typedChar, int keyCode);

	ChatButton getSelectedChatButton();

	void setSelectedChatButton(ChatButton button);

	static ChatScreenExtension active() {
		return (ChatScreenExtension) MinecraftUtils.getChatScreen();
	}

}
