package io.github.solclient.client.platform.mc.text;

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

public interface MutableText extends Text {

	@NotNull MutableText setStyle(@NotNull Style style);

	@NotNull MutableText style(@NotNull UnaryOperator<Style> styler);

}
