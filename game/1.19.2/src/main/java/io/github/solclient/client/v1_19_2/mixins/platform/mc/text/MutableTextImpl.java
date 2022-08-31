package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.MutableText;
import io.github.solclient.client.platform.mc.text.Style;
import net.minecraft.text.Text;

@Mixin(net.minecraft.text.MutableText.class)
@Implements(@Interface(iface = MutableText.class, prefix = "platform$"))
public abstract class MutableTextImpl implements Text {

	public MutableText platform$setStyle(@NotNull Style style) {
		return (MutableText) setStyle((net.minecraft.text.Style) style);
	}

	public @NotNull MutableText platform$style(@NotNull UnaryOperator<Style> styler) {
		return platform$setStyle(styler.apply((Style) getStyle()));
	}

	@Shadow
	public abstract net.minecraft.text.MutableText setStyle(net.minecraft.text.Style style);

}