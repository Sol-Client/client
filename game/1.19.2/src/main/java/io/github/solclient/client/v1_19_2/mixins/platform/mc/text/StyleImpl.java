package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.ClickEvent;
import io.github.solclient.client.platform.mc.text.Style;
import io.github.solclient.client.platform.mc.text.TextColour;
import net.minecraft.text.TextColor;

@Mixin(net.minecraft.text.Style.class)
@Implements(@Interface(iface = Style.class, prefix = "platform$"))
public abstract class StyleImpl {

	public @Nullable TextColour platform$getColour() {
		return (TextColour) (Object) getColor();
	}

	@Shadow
	public abstract TextColor getColor();

	public @NotNull Style platform$withColour(@Nullable TextColour colour) {
		return (Style) withColor((TextColor) (Object) colour);
	}

	@Shadow
	public abstract net.minecraft.text.Style withColor(TextColor textColor);

	public boolean platform$boldFlag() {
		return isBold();
	}

	@Shadow
	public abstract boolean isBold();

	public @NotNull Style platform$withBold(@Nullable Boolean bold) {
		return (Style) withBold(bold);
	}

	@Shadow
	public abstract net.minecraft.text.Style withBold(Boolean bold);

	public boolean platform$italicFlag() {
		return isItalic();
	}

	@Shadow
	public abstract boolean isItalic();

	public @NotNull Style platform$withItalic(@Nullable Boolean italic) {
		return (Style) withItalic(italic);
	}

	@Shadow
	public abstract net.minecraft.text.Style withItalic(Boolean italic);

	public boolean platform$underlinedFlag() {
		return isUnderlined();
	}

	@Shadow
	public abstract boolean isUnderlined();

	public @NotNull Style platform$withUnderlined(@Nullable Boolean underlined) {
		return (Style) withUnderline(underlined);
	}

	@Shadow
	public abstract net.minecraft.text.Style withUnderline(Boolean underlined);

	public boolean platform$strikethroughFlag() {
		return isStrikethrough();
	}

	@Shadow
	public abstract boolean isStrikethrough();

	public @NotNull Style platform$withStrikethrough(@Nullable Boolean strikethrough) {
		return (Style) withStrikethrough(strikethrough);
	}

	@Shadow
	public abstract net.minecraft.text.Style withStrikethrough(Boolean strikethrough);

	public boolean platform$obfuscatedFlag() {
		return isObfuscated();
	}

	@Shadow
	public abstract boolean isObfuscated();

	public @NotNull Style platform$withObfuscated(@Nullable Boolean obfuscated) {
		return (Style) withObfuscated(obfuscated);
	}

	@Shadow
	public abstract net.minecraft.text.Style withObfuscated(Boolean obfuscated);

	public @Nullable ClickEvent platform$getClickEvent() {
		return (ClickEvent) getClickEvent();
	}

	@Shadow
	public abstract net.minecraft.text.ClickEvent getClickEvent();

	public @NotNull Style platform$withClickEvent(@Nullable ClickEvent event) {
		return (Style) withClickEvent((net.minecraft.text.ClickEvent) event);
	}

	@Shadow
	public abstract net.minecraft.text.Style withClickEvent(net.minecraft.text.ClickEvent clickEvent);

}
