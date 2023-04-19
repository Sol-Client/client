package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.extension.ScreenExtension;
import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(ChatScreen.class)
public class ChatScreenMixin implements ScreenExtension {

	@Override
	public boolean canBeForceClosed() {
		if (ChatMod.enabled)
			return !ChatMod.instance.preventClose;

		return true;
	}

}
