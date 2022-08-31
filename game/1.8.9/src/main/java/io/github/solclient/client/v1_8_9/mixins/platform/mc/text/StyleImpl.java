package io.github.solclient.client.v1_8_9.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.ClickEvent;
import io.github.solclient.client.platform.mc.text.Style;
import io.github.solclient.client.platform.mc.text.TextColour;
import io.github.solclient.client.platform.mc.text.TextFormatting;
import io.github.solclient.client.v1_8_9.platform.mc.text.TextColourImpl;
import net.minecraft.util.Formatting;

@Mixin(net.minecraft.text.Style.class)
@Implements(@Interface(iface = Style.class, prefix = "platform$"))
public abstract class StyleImpl {

	@Shadow
	public abstract net.minecraft.text.Style deepCopy();

	public @Nullable TextColour platform$getColour() {
		TextFormatting formatting = (TextFormatting) (Object) getColor();

		if(formatting == null) {
			return null;
		}

		return formatting.getColour();
	}

	@Shadow
	public abstract Formatting getColor();

	public @NotNull Style platform$withColour(@Nullable TextColour colour) {
		if(colour == null) {
			return (Style) deepCopy().setFormatting(null);
		}

		return (Style) deepCopy().setFormatting(((TextColourImpl) colour).getFormatting());
	}

	public boolean platform$boldFlag() {
		return isBold();
	}

	@Shadow
	public abstract boolean isBold();

	public @NotNull Style platform$withBold(@Nullable Boolean bold) {
		return (Style) deepCopy().setBold(bold);
	}

	public boolean platform$italicFlag() {
		return isItalic();
	}

	@Shadow
	public abstract boolean isItalic();

	public @NotNull Style platform$withItalic(@Nullable Boolean italic) {
		return (Style) deepCopy().setItalic(italic);
	}

	public boolean platform$underlinedFlag() {
		return isUnderlined();
	}

	@Shadow
	public abstract boolean isUnderlined();

	public @NotNull Style platform$withUnderlined(@Nullable Boolean underlined) {
		return (Style) deepCopy().setUnderline(underlined);
	}

	public boolean platform$strikethroughFlag() {
		return isStrikethrough();
	}

	@Shadow
	public abstract boolean isStrikethrough();

	public @NotNull Style platform$withStrikethrough(@Nullable Boolean strikethrough) {
		return (Style) deepCopy().setStrikethrough(strikethrough);
	}

	public boolean platform$obfuscatedFlag() {
		return isObfuscated();
	}

	@Shadow
	public abstract boolean isObfuscated();

	public @NotNull Style platform$withObfuscated(@Nullable Boolean obfuscated) {
		return (Style) deepCopy().setObfuscated(obfuscated);
	}

	public @Nullable ClickEvent platform$getClickEvent() {
		return (ClickEvent) getClickEvent();
	}

	@Shadow
	public abstract net.minecraft.text.ClickEvent getClickEvent();

	public @NotNull Style platform$withClickEvent(@Nullable ClickEvent event) {
		return (Style) deepCopy().setClickEvent((net.minecraft.text.ClickEvent) event);
	}

}
