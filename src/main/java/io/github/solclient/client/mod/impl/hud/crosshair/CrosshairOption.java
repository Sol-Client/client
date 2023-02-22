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

package io.github.solclient.client.mod.impl.hud.crosshair;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.PixelMatrix;

public class CrosshairOption extends ModOption<PixelMatrix> {

	CrosshairMod mod;

	public CrosshairOption(CrosshairMod mod) {
		super(mod.getTranslationKey("option.pixels"), ModOptionStorage.of(PixelMatrix.class, () -> mod.pixels));
		this.mod = mod;
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent(190, false);
		container.add(new CrosshairEditorDialog(this), Controller.none());
		return container;
	}

}