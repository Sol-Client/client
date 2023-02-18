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
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class ButtonComponent extends ColouredComponent {

	private IconComponent icon;
	private int width = 100;
	private int height = 20;
	@Getter
	private final Controller<String> text;
	private final Controller<Colour> fg;

	public ButtonComponent(String text, Controller<Colour> colour, Controller<Colour> fg) {
		this((component, defaultText) -> I18n.translate(text), colour, fg);
	}

	public ButtonComponent(Controller<String> text, Controller<Colour> colour, Controller<Colour> fg) {
		super(colour);
		this.text = text;
		this.fg = fg;

		add(new LabelComponent(text, fg), (component, bounds) -> {
			int x = 0;
			int width = getBounds().getWidth();

			if (icon != null && getBounds().getWidth() < icon.getBounds().getEndX() + bounds.getWidth() + 16) {
				x = icon.getBounds().getEndX() + 1;
				width -= x + 7;
			}

			return bounds.offset(x + (width / 2 - bounds.getWidth() / 2),
					getBounds().getHeight() / 2 - bounds.getHeight() / 2);
		});
	}

	@Override
	public void render(ComponentRenderInfo info) {
		// pill
		float radius = getBounds().getHeight() / 2;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, width, height, radius);
		NanoVG.nvgFill(nvg);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(width, height);
	}

	@Override
	public ButtonComponent onClick(ClickHandler onClick) {
		super.onClick(onClick);
		return this;
	}

	public ButtonComponent withIcon(String name) {
		icon = new IconComponent(name, 12, 12, fg);
		add(icon,
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		return this;
	}

	public ButtonComponent width(int width) {
		this.width = width;
		return this;
	}

	public ButtonComponent height(int height) {
		this.height = height;
		return this;
	}

	public static ButtonComponent done(Runnable onClick) {
		return new ButtonComponent("gui.done", theme.accent(), theme.accentFg())
				.onClick((info, button) -> {
					if (button == 0) {
						MinecraftUtils.playClickSound(true);
						onClick.run();

						return true;
					}
					return false;
				}).withIcon("done");
	}

}
