package me.mcblueparrot.client.util.access;

import me.mcblueparrot.client.ui.ChatButton;
import me.mcblueparrot.client.util.Utils;

public interface AccessGuiChat {

	void type(char typedChar, int keyCode);

	ChatButton getSelectedChatButton();

	void setSelectedChatButton(ChatButton button);

	static AccessGuiChat getInstance() {
		return (AccessGuiChat) Utils.getChatGui();
	}

}
