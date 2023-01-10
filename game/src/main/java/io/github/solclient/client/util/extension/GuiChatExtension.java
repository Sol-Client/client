package io.github.solclient.client.util.extension;

import io.github.solclient.client.ui.ChatButton;
import io.github.solclient.client.util.Utils;

public interface GuiChatExtension {

	void type(char typedChar, int keyCode);

	ChatButton getSelectedChatButton();

	void setSelectedChatButton(ChatButton button);

	static GuiChatExtension getInstance() {
		return (GuiChatExtension) Utils.getChatGui();
	}

}
