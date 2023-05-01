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

import java.util.Optional;

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ColourPickerDialog;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;

public class ColourOption extends ModOption<Colour> {

	private final Optional<String> applyToAllKey;

	public ColourOption(String name, ModOptionStorage<Colour> storage, Optional<String> applyToAllKey) {
		super(name, storage);
		this.applyToAllKey = applyToAllKey;
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		ColourBoxComponent colour = new ColourBoxComponent(Controller.of(() -> getValue()));
		container.add(colour, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		container.add(new LabelComponent(Controller.of(() -> getValue().toHexString())).scaled(0.8F),
				new AlignedBoundsController(Alignment.END, Alignment.CENTRE, (component, defaultBounds) -> {
					return new Rectangle(
							(int) (container.getBounds().getWidth() - NanoVGManager.getRegularFont()
									.getWidth(NanoVGManager.getNvg(), ((LabelComponent) component).getText()) - 12),
							defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
				}));

		colour.onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			container.getScreen().getRoot().setDialog(new ColourPickerDialog(this, getValue(), this::setValue));
			return true;
		});

		return container;
	}

	public boolean canApplyToAll() {
		return applyToAllKey.isPresent();
	}

	public void applyToAll() {
		for (Mod mod : SolClient.INSTANCE) {
			for (ColourOption option : mod.getFlatOptions(ColourOption.class)) {
				if (!option.applyToAllKey.equals(applyToAllKey))
					continue;
				option.setFrom(this);
			}
		}
	}

}
