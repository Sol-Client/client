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

import java.util.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.option.impl.SliderOption;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.font.TextRenderer;

/**
 * Represents a mod with only a single HUD.
 */
@AbstractTranslationKey(SolClientHudMod.TRANSLATION_KEY)
public abstract class SolClientHudMod extends StandardMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.hud";

	/**
	 * Represents the single element that this mod contains.
	 */
	protected final HudElement element = new HudModElement();

	@Expose
	private Position position;
	@Expose
	public float scale = 100;
	protected TextRenderer font;

	@Override
	protected List<ModOption<?>> createOptions() {
		List<ModOption<?>> options = super.createOptions();
		options.add(1,
				new SliderOption(TRANSLATION_KEY + ".option.scale",
						ModOptionStorage.of(Number.class, () -> scale, (value) -> scale = value.floatValue()),
						Optional.of("sol_client.slider.percent"), 50, 150, 1));
		return options;
	}

	@Override
	public void lateInit() {
		super.lateInit();
		this.font = mc.textRenderer;
	}

	protected float getScale() {
		return scale / 100;
	}

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(element);
	}

	public void setPosition(Position position) {
		element.setPosition(position);
	}

	public boolean isVisible() {
		return true;
	}

	public Rectangle getBounds(Position position) {
		return null;
	}

	@Override
	public void render(boolean editMode) {
		element.render(editMode);
	}

	public void render(Position position, boolean editMode) {
	}

	public boolean isShownInReplay() {
		return false;
	}

	public Position determineDefaultPosition(int width, int height) {
		return new Position(0, 0);
	}

	class HudModElement implements HudElement {

		@Override
		public Mod getMod() {
			return SolClientHudMod.this;
		}

		@Override
		public Position getConfiguredPosition() {
			return position;
		}

		@Override
		public void setPosition(Position position) {
			SolClientHudMod.this.position = position;
		}

		@Override
		public Position determineDefaultPosition(int width, int height) {
			return SolClientHudMod.this.determineDefaultPosition(width, height);
		}

		@Override
		public float getScale() {
			return scale / 100F;
		}

		@Override
		public boolean isVisible() {
			return isEnabled() && SolClientHudMod.this.isVisible();
		}

		@Override
		public void render(Position position, boolean editMode) {
			SolClientHudMod.this.render(position, editMode);
		}

		@Override
		public boolean isShownInReplay() {
			return SolClientHudMod.this.isShownInReplay();
		}

		@Override
		public Rectangle getBounds(Position position) {
			return SolClientHudMod.this.getBounds(position);
		}

	}

}
