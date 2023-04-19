package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.hud.chat.ChatAnimationData;
import lombok.*;
import net.minecraft.client.gui.hud.ChatHudLine;

@Mixin(ChatHudLine.class)
public class ChatHudLineMixin implements ChatAnimationData {

	@Getter
	@Setter
	private float transparency = 1;

	@Getter
	@Setter
	private float lastTransparency = 1;

}
