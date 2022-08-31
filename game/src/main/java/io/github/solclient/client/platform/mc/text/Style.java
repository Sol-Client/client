package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Style {

	@Nullable TextColour getColour();

	@NotNull Style withColour(@Nullable TextColour colour);

	boolean boldFlag();

	@NotNull Style withBold(@Nullable Boolean bold);

	boolean italicFlag();

	@NotNull Style withItalic(@Nullable Boolean italic);

	boolean underlinedFlag();

	@NotNull Style withUnderlined(@Nullable Boolean underlined);

	boolean strikethroughFlag();

	@NotNull Style withStrikethrough(@Nullable Boolean strikethrough);

	boolean obfuscatedFlag();

	@NotNull Style withObfuscated(@Nullable Boolean obfuscated);

	@Nullable ClickEvent getClickEvent();

	@NotNull Style withClickEvent(@Nullable ClickEvent event);

}
