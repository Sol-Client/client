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

package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.DirtyMapper;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resource.language.I18n;

/**
 * A simple HUD mod that renders a simple string.
 */
@AbstractTranslationKey(SolClientSimpleHudMod.TRANSLATION_KEY)
public abstract class SolClientSimpleHudMod extends SolClientHudMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.simple_hud";

	@Expose
	@Option
	protected boolean background = true;
	@Expose
	@Option
	@ColourKey(ColourKey.BACKGROUND_COLOUR)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected boolean border = false;
	@Expose
	@Option
	@ColourKey(ColourKey.BORDER_COLOUR)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	@ColourKey(ColourKey.TEXT_COLOUR)
	protected Colour textColour = Colour.WHITE;
	@Expose
	@Option
	protected boolean shadow = true;
	private DirtyMapper<String, Integer> langWidth = new DirtyMapper<>(
			() -> mc.getLanguageManager().getLanguage().getCode(), (key) -> {
				String translationKey = getTranslationKey("default_width");
				String width = I18n.translate(translationKey);

				if (width.equals(translationKey)) {
					return 53;
				}

				return Integer.parseInt(width);
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
		if (text != null) {
			if (background) {
				getBounds(position).fill(backgroundColour);
			} else {
				if (!text.isEmpty()) {
					text = "[" + text + "]";
				}
			}

			if (border) {
				getBounds(position).stroke(borderColour);
			}
			font.draw(text, position.getX() + (getBounds(position).getWidth() / 2F) - (font.getStringWidth(text) / 2F),
					position.getY() + 4, textColour.getValue(), shadow);
		}
	}

	public abstract String getText(boolean editMode);

}
