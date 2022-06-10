package io.github.solclient.api.text;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface Component extends Cloneable {

	@NotNull List<Component> getSiblings();

	void append(@NotNull Component component);

	@NotNull Component clone();

	@NotNull String getRaw();

	@NotNull Style getStyle();

	void setStyle(@NotNull Style style);

}
