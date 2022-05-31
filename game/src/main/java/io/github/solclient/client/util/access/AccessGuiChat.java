package io.github.solclient.client.util.access;

import io.github.solclient.client.ui.ChatButton;
import io.github.solclient.client.util.Utils;

public interface AccessGuiChat {

	void type(char typedChar, int keyCode);

	ChatButton getSelectedChatButton();

	void setSelectedChatButton(ChatButton button);

	static AccessGuiChat getInstance() {
		return (AccessGuiChat) Utils.getChatGui();
	}

}
