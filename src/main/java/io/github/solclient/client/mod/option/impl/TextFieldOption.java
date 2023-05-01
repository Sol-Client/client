/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
		Component container = createDefaultComponent(20, true);

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
