package me.mcblueparrot.client.ui.component.impl;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.data.Colour;

@AllArgsConstructor
public class ColouredComponent extends Component {

	private final Controller<Colour> colour;

	public Colour getColour() {
		return colour.get(this, Colour.WHITE);
	}

	public int getColourValue() {
		return getColour().getValue();
	}

}
