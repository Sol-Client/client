package io.github.solclient.client.v1_19_2.mixins.platform.mc.gui.chat;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.hud.chat.*;
import io.github.solclient.client.platform.mc.text.Text;
import net.minecraft.client.gui.hud.*;

@Mixin(ChatHud.class)
@Implements(@Interface(iface = Chat.class, prefix = "platform$"))
public abstract class ChatImpl {

	public void platform$resetChat() {
		reset();
	}

	@Shadow
	public abstract void reset();

	public void platform$scrollToStart() {
		resetScroll();
	}

	@Shadow
	public abstract void resetScroll();

	public int platform$getLineCount() {
		return getVisibleLineCount();
	}

	@Shadow
	public abstract int getVisibleLineCount();

	public @NotNull List<ChatMessage> platform$getVisibleMessages() {
		return visibleMessages;
	}

	@Shadow
	private @Final List<ChatMessage> visibleMessages;

	public boolean platform$isOpen() {
		return isChatFocused();
	}

	@Shadow
	public abstract boolean isChatFocused();

	public int platform$getChatWidth() {
		return getWidth();
	}

	@Shadow
	public abstract int getWidth();

	public int platform$getScroll() {
		return scrolledLines;
	}

	@Shadow
	private int scrolledLines;

	public void platform$scrollChat(int amount) {
		scroll(amount);
	}

	@Shadow
	public abstract void scroll(int lines);

	public boolean platform$isScrolled() {
		return hasUnreadNewMessages;
	}

	@Shadow
	private boolean hasUnreadNewMessages;

	public void platform$addMessage(@NotNull String text) {
		addMessage(net.minecraft.text.Text.literal(text));
	}

	public void platform$addMessage(@NotNull Text text) {
		addMessage((net.minecraft.text.Text) text);
	}

	@Shadow
	public abstract void addMessage(net.minecraft.text.Text message);

}

