package io.github.solclient.client.v1_19_2.mixins.gui.hud;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.hud.chat.ChatAnimationData;
import lombok.*;
import net.minecraft.client.gui.hud.ChatHudLine;

@Getter
@Setter
@Mixin(ChatHudLine.Visible.class)
public class ChatAnimationDataMixin implements ChatAnimationData {

	private float transparency = 1, lastTransparency = 1;

}
