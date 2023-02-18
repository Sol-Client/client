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

import java.text.DecimalFormat;
import java.util.Optional;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.NanoVGManager;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class SliderOption extends ModOption<Float> {

	public SliderOption(String name, ModOptionStorage<Float> storage,
			Optional<String> valueFormat, float min, float max, float step) {
		super(name, storage);
		this.valueFormat = valueFormat;
		this.min = min;
		this.max = max;
		this.step = step;
	}

	@Getter
	private final float min, max, step;
	@Getter
	private Optional<String> valueFormat;

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();
		container.add(new SliderComponent(min, max, step, getValue(), this::setValue),
				new AlignedBoundsController(Alignment.END, Alignment.CENTRE));
		valueFormat.ifPresent((format) -> {
			container.add(
					new LabelComponent((component, defaultText) -> I18n.translate(format,
							new DecimalFormat("0.##").format(getValue()))).scaled(0.8F),
					new AlignedBoundsController(Alignment.END, Alignment.CENTRE, (component, defaultBounds) -> {
						return new Rectangle(
								(int) (container.getBounds().getWidth() - NanoVGManager.getRegularFont()
										.getWidth(NanoVGManager.getNvg(), ((LabelComponent) component).getText()) - 97),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
					}));
		});
		return container;
	}

}
