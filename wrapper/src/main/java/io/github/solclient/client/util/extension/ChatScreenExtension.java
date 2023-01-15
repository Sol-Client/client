package io.github.solclient.client.util.extension;

import io.github.solclient.client.chatextensions.ChatButton;
import io.github.solclient.client.util.Utils;

public interface ChatScreenExtension {

	void type(char typedChar, int keyCode);

	ChatButton getSelectedChatButton();

	void setSelectedChatButton(ChatButton button);

	static ChatScreenExtension active() {
		return (ChatScreenExtension) Utils.getChatScreen();
	}

}
