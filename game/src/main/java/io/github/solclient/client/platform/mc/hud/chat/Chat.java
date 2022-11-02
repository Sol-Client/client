package io.github.solclient.client.platform.mc.hud.chat;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.chat.ChatAccessor;
import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.Text;

public interface Chat extends ChatAccessor {

	void resetChat();

	void scrollToStart();

	int getLineCount();

	@NotNull
	List<ChatMessage> getVisibleMessages();

	boolean isOpen();

	int getChatWidth();

	int getScroll();

	void scrollChat(int amount);

	boolean isScrolled();

	@Helper
	static @NotNull Chat requireInstance() {
		return getInstance().orElseThrow(() -> new IllegalStateException("Chat not open"));
	}

	@Helper
	static @NotNull Optional<Chat> getInstance() {
		return MinecraftClient.getInstance().getScreen(Chat.class);
	}

	void addMessage(@NotNull String text);

	void addMessage(@NotNull Text text);

}
