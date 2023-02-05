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

import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.data.Colour;

public class Theme implements Cloneable {

	public static final Theme DARK;

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
		DARK.accentFg = new Colour(0xFF000000);
		DARK.fgButton = new Colour(0xFFFFFFFF);
		DARK.fgButtonHover = new Colour(0xFFDDDDDD);
		DARK.transparent1 = new Colour(0xFF282828);
		DARK.transparent2 = new Colour(0xFF3c3c3c);
		DARK.danger = new Colour(0xFFFF2929);
		DARK.dangerHover = new Colour(0xFFFF4B4B);
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

	// @formatter:off
	public final Controller<Colour> bg() { return Controller.of(bg); }
	public final Controller<Colour> fg() { return Controller.of(fg); }
	public final Controller<Colour> accentFg() { return Controller.of(accentFg); }
	// @formatter:on

	private Controller<Colour> hoverPair(Colour base, Colour hover) {
		return new AnimatedColourController((component, defaultColour) -> component.isHovered() ? hover : base);
	}

	public final Controller<Colour> button() {
		return hoverPair(button, buttonHover);
	}

	public final Controller<Colour> buttonSecondary() {
		return hoverPair(buttonSecondary, buttonSecondaryHover);
	}

	public final Controller<Colour> fgButton() {
		return new AnimatedColourController(
				(component, defaultColour) -> component.isHovered() ? fgButtonHover : fgButton);
	}

	public final Controller<Colour> accent() {
		return hoverPair(accent, accentHover);
	}

	public final Controller<Colour> danger() {
		return hoverPair(danger, dangerHover);
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
