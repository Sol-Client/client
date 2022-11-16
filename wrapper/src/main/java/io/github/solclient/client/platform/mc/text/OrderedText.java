package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

/**
 * Used because of new MC versions being stupid.
 * In 1.8, this is just fancy clothing for Text.
 */
public interface OrderedText {

	default @NotNull OrderedText getPlainOrdered() {
		return (OrderedText) Text.literal(((Text) this).getPlain());
	}

}
