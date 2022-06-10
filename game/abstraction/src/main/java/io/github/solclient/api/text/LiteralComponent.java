package io.github.solclient.api.text;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface LiteralComponent extends Component {

	static @NotNull LiteralComponent create(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getText();
}
