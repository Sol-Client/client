package io.github.solclient.client.util.extension;

import java.util.List;

import net.minecraft.client.gui.ChatLine;

public interface GuiNewChatExtension {

	List<ChatLine> getDrawnChatLines();

	boolean getIsScrolled();

	int getScrollPos();

	void clearChat();

}
