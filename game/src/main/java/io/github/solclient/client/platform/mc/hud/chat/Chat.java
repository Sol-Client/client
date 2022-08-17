package io.github.solclient.client.platform.mc.hud.chat;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.chat.ChatAccessor;
import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.Text;

public interface Chat extends ChatAccessor {

	void refresh();

	void resetScroll();

	int getVisibleMessageCount();

	@NotNull
	List<ChatMessage> getVisibleMessages();

	boolean isOpen();

	int getWidth();

	int getScroll();

	void scroll(int amount);

	boolean isScrolled();

	@Helper
	static @NotNull Chat requireInstance() {
		return getInstance().orElseThrow(() -> new UnsupportedOperationException("Chat not open"));
	}

	@Helper
	static @NotNull Optional<Chat> getInstance() {
		return MinecraftClient.getInstance().getScreen(Chat.class);
	}

	void addMessage(@NotNull String text);

	void addMessage(@NotNull Text text);

}
