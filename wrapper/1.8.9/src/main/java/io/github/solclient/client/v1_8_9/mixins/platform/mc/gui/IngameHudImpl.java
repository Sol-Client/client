package io.github.solclient.client.v1_8_9.mixins.platform.mc.gui;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.hud.IngameHud;
import io.github.solclient.client.platform.mc.hud.chat.Chat;
import net.minecraft.client.gui.hud.*;

@Mixin(InGameHud.class)
@Implements(@Interface(iface = IngameHud.class, prefix = "platform$"))
public abstract class IngameHudImpl {

	public @NotNull Chat platform$getChat() {
		return (Chat) getChatHud();
	}

	@Shadow
	public abstract ChatHud getChatHud();

	public int platform$getTickCounter() {
		return getTicks();
	}

	@Shadow
	public abstract int getTicks();

}

