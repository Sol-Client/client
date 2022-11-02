package io.github.solclient.client.v1_8_9.mixins.gui.hud;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.hud.chat.ChatAnimationData;
import lombok.*;
import net.minecraft.client.gui.hud.ChatHudLine;

@Getter
@Setter
@Mixin(ChatHudLine.class)
public class ChatAnimationDataMixin implements ChatAnimationData {

	private float transparency = 1, lastTransparency = 1;

}
