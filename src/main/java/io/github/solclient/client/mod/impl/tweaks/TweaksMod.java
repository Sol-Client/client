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

package io.github.solclient.client.mod.impl.tweaks;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GammaEvent;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.annotation.Slider;
import lombok.Getter;

public class TweaksMod extends SolClientMod {

	public static boolean enabled;
	public static TweaksMod instance;

	@Expose
	@Option
	private boolean fullbright;
	@Expose
	@Option
	public boolean showOwnTag;
	@Expose
	@Option
	public boolean arabicNumerals;
	@Expose
	@Option
	public boolean betterTooltips = true;
	@Expose
	@Option
	public boolean minimalViewBobbing;
	@Expose
	@Option
	public boolean minimalDamageShake;
	@Expose
	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	private float damageShakeIntensity = 100;
	@Expose
	@Option
	@Slider(min = 0, max = 0.5F, step = 0.01F)
	public float lowerFireBy;
	@Expose
	@Option
	public boolean disableBlockParticles;
	@Expose
	@Option
	public boolean confirmDisconnect;
	@Expose
	@Option
	public boolean betterKeyBindings = true;
	@Expose
	@Option
	public boolean disableHotbarScrolling;
	@Expose
	@Option
	public boolean centredInventory = true;
	@Expose
	@Option
	public boolean reconnectButton = true;
	@Expose
	@Option
	boolean borderlessFullscreen;
	@Expose
	@Option
	public boolean rawInput;

	private final BorderlessFullscreen borderlessFullscreenManager = new BorderlessFullscreen(this);
	@Getter
	private final RawInput rawInputManager = new RawInput(this);

	@Override
	public String getId() {
		return "tweaks";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.GENERAL;
	}

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
		if (borderlessFullscreen) {
			Client.INSTANCE.getEvents().register(borderlessFullscreenManager);

			if (mc.isFullscreen())
				borderlessFullscreenManager.update(true);
		}
		if (rawInput)
			rawInputManager.start();
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		if (borderlessFullscreen) {
			Client.INSTANCE.getEvents().unregister(borderlessFullscreenManager);

			if (mc.isFullscreen()) {
				borderlessFullscreenManager.update(false);
				mc.toggleFullscreen();
				mc.toggleFullscreen();
			}
		}
		if (rawInput)
			rawInputManager.stop();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if (!isEnabled())
			return;

		if (key.equals("borderlessFullscreen") && mc.isFullscreen()) {
			if ((boolean) value) {
				Client.INSTANCE.getEvents().register(borderlessFullscreenManager);
				borderlessFullscreenManager.update(true);
			} else {
				Client.INSTANCE.getEvents().unregister(borderlessFullscreenManager);
				borderlessFullscreenManager.update(false);
				mc.toggleFullscreen();
				mc.toggleFullscreen();
			}
		}
		if (key.equals("rawInput")) {
			if ((boolean) value)
				rawInputManager.start();
			else
				rawInputManager.stop();
		}
	}

	@EventHandler
	public void onGamma(GammaEvent event) {
		if (fullbright) {
			event.gamma = 20F;
		}
	}

	public float getDamageShakeIntensity() {
		return damageShakeIntensity / 100;
	}

}
