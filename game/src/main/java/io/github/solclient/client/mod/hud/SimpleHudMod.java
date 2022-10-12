package io.github.solclient.client.mod.hud;

import java.util.Optional;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.util.ChainCache;
import io.github.solclient.client.util.data.*;

/**
 * A simple HUD mod that rendered a simple string.
 */
@AbstractTranslationKey(SimpleHudMod.TRANSLATION_KEY)
public abstract class SimpleHudMod extends HudMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.simple_hud";

	@Expose
	@Option
	protected boolean background = true;
	@Expose
	@Option(applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected boolean border = false;
	@Expose
	@Option(applyToAllClass = Option.BORDER_COLOUR_CLASS)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option(applyToAllClass = Option.TEXT_COLOUR_CLASS)
	protected Colour textColour = Colour.WHITE;
	@Expose
	@Option
	protected boolean shadow = true;

	private ChainCache<String, Integer> langWidth = new ChainCache<String, Integer>(() -> mc.getLanguageManager().getCode(), (key) -> {
		String translationKey = getTranslationKey() + ".default_width";
		Optional<String> width = I18n.translateOpt(translationKey);

		if(!width.isPresent()) {
			return 53;
		}

		return Integer.parseInt(width.get());
	});

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX(), position.getY(), getWidth(), 16);
	}

	private int getWidth() {
		return langWidth.get();
	}

	@Override
	public void render(Position position, boolean editMode) {
		String text = getText(editMode);
		if(text != null) {
			if(background) {
				getBounds(position).fill(backgroundColour);
			}
			else {
				if(!text.isEmpty()) {
					text = "[" + text + "]";
				}
			}

			if(border) {
				getBounds(position).stroke(borderColour);
			}
			font.render(text,
					(int) (position.getX() + (getBounds(position).getWidth() / 2F) - (font.getTextWidth(text) / 2F)),
					position.getY() + 4, textColour.getValue(), shadow);
		}
	}

	public abstract String getText(boolean editMode);

}
