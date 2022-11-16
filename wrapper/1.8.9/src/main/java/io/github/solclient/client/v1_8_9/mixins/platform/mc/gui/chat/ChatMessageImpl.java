package io.github.solclient.client.v1_8_9.mixins.platform.mc.gui.chat;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.hud.chat.ChatMessage;
import io.github.solclient.client.platform.mc.text.*;
import net.minecraft.client.gui.hud.ChatHudLine;

@Mixin(ChatHudLine.class)
@Implements(@Interface(iface = ChatMessage.class, prefix = "platform$"))
public abstract class ChatMessageImpl implements ChatMessage {

	@Override
	public int getMessageCreationTick() {
		return getCreationTick();
	}

	@Shadow
	public abstract int getCreationTick();

	@Override
	public @NotNull OrderedText getMessage() {
		return (OrderedText) getText();
	}

	@Shadow
	public abstract net.minecraft.text.Text getText();

}
