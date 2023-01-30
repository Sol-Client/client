package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(ChatScreen.class)
public interface ChatScreenAccessor {

	@Invoker("keyPressed")
	public abstract void type(char character, int code);

}
