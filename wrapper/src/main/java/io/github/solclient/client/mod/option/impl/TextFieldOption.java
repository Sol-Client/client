package io.github.solclient.client.mod.option.impl;

import java.util.Objects;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.TextFieldComponent;
import io.github.solclient.client.util.data.Alignment;

public class TextFieldOption extends ModOption<String> {

	private final String placeholder;

	public TextFieldOption(String name, ModOptionStorage<String> storage, String placeholder) {
		super(name, storage);
		this.placeholder = Objects.requireNonNull(placeholder);
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		TextFieldComponent field = new TextFieldComponent(100, false).withPlaceholder(placeholder)
				.onUpdate((string) -> {
					setValue(string);
					return true;
				});
		field.autoFlush();
		field.setText(getValue());

		container.add(container, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));
		return container;
	}

}
