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

package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resource.language.I18n;

public class LabelComponent extends ColouredComponent {

	private final Controller<String> text;
	private float scale = 1;

	public LabelComponent(String text) {
		this((component, defaultText) -> I18n.translate(text), (component, defaultColour) -> defaultColour);
	}

	public LabelComponent(Controller<String> text) {
		this(text, (component, defaultColour) -> defaultColour);
	}

	public LabelComponent(Controller<String> text, Controller<Colour> colour) {
		super(colour);
		this.text = text;
	}

	public LabelComponent scaled(float scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		regularFont.withSize((int) (regularFont.getSize() * scale),
				() -> regularFont.renderString(nvg, getText(), 0, 0));

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return regularFont.withSize((int) (regularFont.getSize() * scale),
				() -> Rectangle.ofDimensions((int) (regularFont.getWidth(nvg, getText()) * scale),
						(int) ((regularFont.getLineHeight(nvg) + (2 * scale)))));
	}

	public String getText() {
		return text.get(this);
	}

}
