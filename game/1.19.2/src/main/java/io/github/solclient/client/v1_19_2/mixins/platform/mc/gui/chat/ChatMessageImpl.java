package io.github.solclient.client.v1_19_2.mixins.platform.mc.gui.chat;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.hud.chat.ChatMessage;
import io.github.solclient.client.platform.mc.text.*;
import net.minecraft.client.gui.hud.ChatHudLine;

@Mixin(ChatHudLine.Visible.class)
@Implements(@Interface(iface = ChatMessage.class, prefix = "platform$"))
public abstract class ChatMessageImpl implements ChatMessage {

	@Override
	public int getMessageCreationTick() {
		return addedTime();
	}

	@Shadow
	public abstract int addedTime();

	@Override
	public @NotNull OrderedText getMessage() {
		return (OrderedText) content();
	}

	@Shadow
	public abstract net.minecraft.text.OrderedText content();

}
