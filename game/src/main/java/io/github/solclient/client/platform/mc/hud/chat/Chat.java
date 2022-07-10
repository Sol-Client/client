package io.github.solclient.client.platform.mc.hud.chat;

import java.util.List;

public interface Chat {

	void refresh();

	void resetScroll();

	int getVisibleMessageCount();

	List<ChatMessage> getVisibleMessages();

	boolean isOpen();

	int getWidth();

	int getScroll();

	void scroll(int amount);

	boolean isScrolled();

}
