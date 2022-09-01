package io.github.solclient.client.v1_8_9.mixins.platform.mc.text;

import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.MutableText;
import io.github.solclient.client.platform.mc.text.Style;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.util.Utils;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Mixin(net.minecraft.text.Text.class)
@Implements({ @Interface(iface = Text.class, prefix = "platform$"), @Interface(iface = MutableText.class, prefix = "mut$") })
public interface TextImpl {

	@NotNull
	@SuppressWarnings({ "rawtypes", "unchecked" })
	default List<Text> platform$getExtraText() {
		return (List) getSiblings();
	}

	@Shadow
	List<net.minecraft.text.Text> getSiblings();

	default @NotNull Text platform$clone() {
		return (Text) copy();
	}

	@Shadow
	net.minecraft.text.Text copy();

	default @NotNull String platform$getPlain() {
		return Formatting.strip(getString());
	}

	@Shadow
	String getString();

	default @NotNull Style platform$getStyle() {
		return (Style) getStyle();
	}

	@Shadow
	net.minecraft.text.Style getStyle();

	default @NotNull MutableText mut$setStyle(@NotNull Style style) {
		return (MutableText) setStyle((net.minecraft.text.Style) style);
	}

	@Shadow
	net.minecraft.text.Text setStyle(net.minecraft.text.Style par1);

	default @NotNull MutableText mut$style(@NotNull UnaryOperator<Style> styler) {
		return mut$setStyle(styler.apply(platform$getStyle()));
	}

}

@Mixin(Text.class)
interface TextImpl$Static {

	@Overwrite(remap = false)
	static @NotNull MutableText literal(@NotNull String text) {
		return (MutableText) new LiteralText(text);
	}

	@Overwrite(remap = false)
	static @NotNull MutableText format(@NotNull String fmt, Object... args) {
		return (MutableText) new LiteralText(Utils.format(fmt, args));
	}

	@Overwrite(remap = false)
	static @NotNull MutableText translation(String key, Object... args) {
		return (MutableText) new TranslatableText(key, args);
	}

}