package io.github.solclient.api.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Style {

	@NotNull ChatColour getColour();

	void setColour(@NotNull ChatColour colour);

}
