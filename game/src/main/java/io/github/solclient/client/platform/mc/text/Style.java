package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Style {

	@Nullable TextColour getColour();

	@NotNull Style withColour(@Nullable TextColour colour);

	boolean isBold();

	@NotNull Style withBold(@Nullable Boolean bold);

	boolean isItalic();

	@NotNull Style withItalic(@Nullable Boolean italic);

	boolean isUnderlined();

	@NotNull Style withUnderlined(@Nullable Boolean underlined);

	boolean isStrikethrough();

	@NotNull Style withStrikethrough(@Nullable Boolean strikethrough);

	boolean isObfuscated();

	@NotNull Style withObfuscated(@Nullable Boolean obfuscated);

	@Nullable ClickEvent getClickEvent();

	@NotNull Style withClickEvent(@Nullable ClickEvent event);

}
