package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

public interface Style {

	@NotNull TextColour getColour();

	void setColour(@NotNull TextColour colour);

	boolean isBold();

	void setBold(boolean bold);

	boolean isItalic();

	void setItalic(boolean italic);

	boolean isUnderlined();

	void setUnderlined(boolean underlined);

	boolean isStrikethrough();

	void setStrikethrough(boolean strikethrough);

	boolean isObfuscated();

	void setObfuscated(boolean obfuscated);

	ClickEvent getClickEvent();

	void setClickEvent(ClickEvent event);

}
