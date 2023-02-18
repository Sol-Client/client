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
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.font.TextRenderer;

/**
 * Represents a mod with only a single HUD.
 */
@AbstractTranslationKey(SolClientHudMod.TRANSLATION_KEY)
public abstract class SolClientHudMod extends SolClientMod implements PrimaryIntegerSettingMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.hud";

	/**
	 * Represents the single element that this mod contains.
	 */
	protected final HudElement element = new HudModElement();

	@Expose
	private Position position;
	@Expose
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	public float scale = 100;
	protected TextRenderer font;

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
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

	@Override
	public void decrement() {
		scale = Math.max(50, scale - 10);
	}

	@Override
	public void increment() {
		scale = Math.min(150, scale + 10);
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
