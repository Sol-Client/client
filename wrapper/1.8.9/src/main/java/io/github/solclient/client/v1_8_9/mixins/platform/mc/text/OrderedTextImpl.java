package io.github.solclient.client.v1_8_9.mixins.platform.mc.text;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.text.OrderedText;

@Mixin(net.minecraft.text.Text.class)
public interface OrderedTextImpl extends OrderedText {

}
