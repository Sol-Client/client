package io.github.solclient.client.mod.option.impl;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ToggleComponent;
import io.github.solclient.client.util.data.Alignment;

public class ToggleOption extends ModOption<Boolean> {

	public ToggleOption(String name, ModOptionStorage<Boolean> storage) {
		super(name, storage);
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();
		container.add(new ToggleComponent(getValue(), this::setValue), new AlignedBoundsController(Alignment.END, Alignment.CENTRE));
		return container;
	}

}
