package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.Colour;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ColouredComponent extends Component {

	private final Controller<Colour> colour;

	public Colour getColour() {
		return colour.get(this, Colour.WHITE);
	}

	public int getColourValue() {
		return getColour().getValue();
	}

}
