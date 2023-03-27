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

package io.github.solclient.client.ui;

import java.util.function.Supplier;

import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.data.Colour;
import lombok.*;

public class Theme implements Cloneable {

	public static final Theme DARK, LIGHT;
	@Setter
	@Getter
	private static Theme current;

	static {
		DARK = new Theme();
		DARK.bg = new Colour(0xFF1F1F1F);
		DARK.button = new Colour(0xFF2A2A2A);
		DARK.buttonHover = new Colour(0xFF2E2E2E);
		DARK.buttonSecondary = new Colour(0xFF383838);
		DARK.buttonSecondaryHover = new Colour(0xFF3C3C3C);
		DARK.fg = new Colour(0xFFFFFFFF);
		DARK.accent = new Colour(0xFFFFB400);
		DARK.accentHover = new Colour(0xFFFFCD26);
		DARK.accentFg = new Colour(0xFF1F1F1F);
		DARK.fgButton = new Colour(0xFFFFFFFF);
		DARK.fgButtonHover = new Colour(0xFFD4D4D4);
		DARK.transparent1 = new Colour(0xFF282828);
		DARK.transparent2 = new Colour(0XFF3C3C3C);
		DARK.danger = new Colour(0xFFFF2929);
		DARK.dangerHover = new Colour(0xFFFF4B4B);

		LIGHT = DARK.clone();
		LIGHT.bg = new Colour(0xFFEBEBEB);
		LIGHT.button = new Colour(0xFFDFDFDF);
		LIGHT.buttonHover = new Colour(0xFFD9D9D9);
		LIGHT.buttonSecondary = new Colour(0xFFCCCCCC);
		LIGHT.buttonSecondaryHover = new Colour(0xFFC7C7C7);
		LIGHT.fg = new Colour(0xFF2A2A2A);
		LIGHT.accentHover = new Colour(0xFFFAA000);
		LIGHT.fgButton = new Colour(0xFF2A2A2A);
		LIGHT.fgButtonHover = new Colour(0xFF454545);

		current = DARK;
	}

	public Colour bg;
	public Colour button;
	public Colour buttonHover;
	public Colour buttonSecondary;
	public Colour buttonSecondaryHover;
	public Colour fgButton;
	public Colour fgButtonHover;
	public Colour fg;
	public Colour accent;
	public Colour accentHover;
	public Colour accentFg;
	public Colour transparent1;
	public Colour transparent2;
	public Colour danger;
	public Colour dangerHover;

	public static Controller<Colour> bg() {
		return new AnimatedColourController(Controller.of(() -> current.bg));
	}

	public static Controller<Colour> fg() {
		return new AnimatedColourController(Controller.of(() -> current.fg));
	}

	public static Controller<Colour> accentFg() {
		return new AnimatedColourController(Controller.of(() -> current.accentFg));
	}

	private static Controller<Colour> hoverPair(Supplier<Colour> base, Supplier<Colour> hover) {
		return new AnimatedColourController((component, defaultColour) -> component.isHovered() ? hover.get() : base.get());
	}

	public static Controller<Colour> button() {
		return hoverPair(() -> current.button, () -> current.buttonHover);
	}

	public static Controller<Colour> buttonSecondary() {
		return hoverPair(() -> current.buttonSecondary, () -> current.buttonSecondaryHover);
	}

	public static Controller<Colour> fgButton() {
		return hoverPair(() -> current.fgButton, () -> current.fgButtonHover);
	}

	public static Controller<Colour> accent() {
		return hoverPair(() -> current.accent, () -> current.accentHover);
	}

	public static Controller<Colour> danger() {
		return hoverPair(() -> current.danger, () -> current.dangerHover);
	}

	@Override
	public Theme clone() {
		try {
			return (Theme) super.clone();
		} catch (CloneNotSupportedException error) {
			throw new AssertionError("Theme should implement Clonable");
		}
	}

}
