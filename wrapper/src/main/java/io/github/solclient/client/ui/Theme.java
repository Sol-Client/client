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
		DARK.accentHover = new Colour(0xFFFFE255);
		DARK.accentFg = DARK.bg;
		DARK.fgButton = new Colour(0xFFFFFFFF);
		DARK.fgButtonHover = new Colour(0xFFDDDDDD);
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

	// @formatter:off
	public final Controller<Colour> bg() { return Controller.of(bg); }
	public final Controller<Colour> fg() { return Controller.of(fg); }
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

	public Controller<Colour> fgButton() {
		return new AnimatedColourController(
				(component, defaultColour) -> component.isHovered() ? fgButtonHover : fgButton);
	}

	public final Controller<Colour> accent() {
		return hoverPair(accent, accentHover);
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
