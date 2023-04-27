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

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.AbstractHudElement;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.AbstractTranslationKey;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;

/**
 * Represents a mod with only a single HUD.
 */
@AbstractTranslationKey(SolClientHudMod.TRANSLATION_KEY)
public abstract class SolClientHudMod extends StandardMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.hud";

	/**
	 * Represents the single element that this mod contains.
	 */
    @Expose
	protected final HudModElement element = new HudModElement();

	protected TextRenderer font;

    @Override
    public void registerTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(element.getClass(), (InstanceCreator<HudModElement>) (type) -> element);
    }

    @Override
	protected List<ModOption<?>> createOptions() {
		List<ModOption<?>> options = super.createOptions();
        options.addAll(1, element.createOptions());
		return options;
	}

	@Override
	public void lateInit() {
		super.lateInit();
		this.font = mc.textRenderer;
	}

    public boolean isDynamic() {
        return false;
    }

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(element);
	}

	public boolean isVisible() {
		return true;
	}

    public float getScale() {
        return element.getScale();
    }

	public abstract Rectangle getBounds(Position position, boolean editMode);

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
    public void loadConfig(JsonObject config) {
        // Migrate old format for positions to this new fancy one
        JsonObject elementObj = null;
        if (!config.has("element")) {
            elementObj = new JsonObject();
            config.add("element", elementObj);
        }
        if (elementObj != null) {
            JsonElement jsonScale = config.remove("scale");
            if (jsonScale != null) {
                elementObj.add("scale", jsonScale);
            }
            Window window = new Window(MinecraftClient.getInstance());
            JsonElement jsonX = config.remove("y");
            if (jsonX != null) {
                elementObj.addProperty("x", window.getScaledHeight() > 0 ? jsonX.getAsInt() / window.getScaledWidth() : 0);
            }
            JsonElement jsonY = config.remove("y");
            if (jsonY != null) {
                elementObj.addProperty("y", window.getScaledHeight() > 0 ? jsonY.getAsInt() / window.getScaledHeight() : 0);
            }
        }
        super.loadConfig(config);
    }

    protected class HudModElement extends AbstractHudElement {

		@Override
		public Mod getMod() {
			return SolClientHudMod.this;
		}

		@Override
		public Position determineDefaultPosition(int width, int height) {
			return SolClientHudMod.this.determineDefaultPosition(width, height);
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
		public Rectangle getBounds(Position position, boolean editMode) {
			return SolClientHudMod.this.getBounds(position, editMode);
		}

        public void setScale(float scale) {
            this.scale = scale;
        }

        @Override
        public boolean isDynamic() {
            return SolClientHudMod.this.isDynamic();
        }
    }

}
