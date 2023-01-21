package io.github.solclient.client.util.extension;

import java.util.List;

import net.minecraft.client.gui.hud.ChatHudLine;

public interface ChatHudExtension {

	List<ChatHudLine> getVisibleMessages();

	boolean getHasUnreadNewMessages();

	int getScrolledLines();

	void clearChat();

}
