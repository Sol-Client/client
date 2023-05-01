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

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;

public class EnumOption<E extends Enum<?>> extends ModOption<E> {

	@Getter
	private final E[] values;

	public EnumOption(String name, ModOptionStorage<E> storage) {
		super(name, storage);
		if (!storage.getType().isEnum())
			throw new IllegalArgumentException(storage + " is not an enum");
		values = storage.getType().getEnumConstants();
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent(20, true);
		int width = getWidth();

		Component label = new LabelComponent((component, defaultText) -> getValue().toString(),
				new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? Theme.getCurrent().fgButtonHover
								: Theme.getCurrent().fgButton));
		Component previous = new IconComponent("prev", 8, 8,
				new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? Theme.getCurrent().fgButtonHover
								: Theme.getCurrent().fgButton));
		Component next = new IconComponent("next", 8, 8,
				new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? Theme.getCurrent().fgButtonHover
								: Theme.getCurrent().fgButton));

		container.add(label,
				new AlignedBoundsController(Alignment.END, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(
								container.getBounds().getWidth() - width - 11 + (width / 2)
										- ((int) NanoVGManager.getRegularFont().getWidth(NanoVGManager.getNvg(),
												((LabelComponent) component).getText()) / 2),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));

		container.add(previous,
				new AlignedBoundsController(Alignment.END, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - width - 12,
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));

		container.add(next, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		container.onClick((info, button) -> {
			if (button != 0 || !(previous.isHovered() || next.isHovered() || label.isHovered()))
				return false;

			MinecraftUtils.playClickSound(true);

			int current = ((Enum<?>) getValue()).ordinal();

			boolean direction = false;

			if (Screen.hasShiftDown())
				direction = !direction;
			if (previous.isHovered())
				direction = !direction;

			if (direction) {
				current--;

				if (current < 0)
					current = values.length - 1;
			} else {
				current++;
				if (current > values.length - 1)
					current = 0;
			}

			setValue(values[current]);

			return true;
		});
		return container;
	}

	private int getWidth() {
		int width = 0;
		for (E field : values) {
			int newWidth = (int) NanoVGManager.getRegularFont().getWidth(NanoVGManager.getNvg(), field.toString());

			if (newWidth > width)
				width = newWidth;
		}
		return width;
	}

}
