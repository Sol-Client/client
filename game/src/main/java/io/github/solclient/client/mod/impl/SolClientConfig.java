package io.github.solclient.client.mod.impl;

import java.io.IOException;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.ConfigOnlyMod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.data.Colour;

public class SolClientConfig extends ConfigOnlyMod {

	public static SolClientConfig instance;

	@Expose
	@Option
	public boolean fancyMainMenu, logoInInventory;

	@Option
	public KeyBinding modsKey = KeyBinding.create(getTranslationKey() + ".mods", Input.RIGHT_SHIFT,
			Client.KEY_CATEGORY),
			editHudKey = KeyBinding.create(getTranslationKey() + ".edit_hud", Input.NONE, Client.KEY_CATEGORY);

	@Expose
	@Option
	public Colour uiColour = new Colour(255, 180, 0);
	public Colour uiHover;

	@Expose
	@Option
	public boolean smoothUIColours = true,
			roundedUI = true,
			buttonClicks = true,
			smoothScrolling = true,
			fancyFont = true;

	private Font font;
	private boolean fontErr;

	@Override
	public String getId() {
		return "sol_client";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.GENERAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();

		instance = this;
		mc.getOptions().addKey(modsKey);
		mc.getOptions().addKey(editHudKey);
		uiHover = getUiHover();
	}

	@Override
	public void postOptionChange(String key, Object value) {
		super.postOptionChange(key, value);

		if(key.equals("fancyFont") && mc.getScreen() instanceof ModsScreen) {
			ModsScreen screen = (ModsScreen) mc.getScreen();
			screen.updateFont();
		}

		if(key.equals("uiColour")) {
			uiHover = getUiHover();
		}
	}

	private Colour getUiHover() {
		return uiColour.add(40);
	}

	public Font getUIFont() {
		if(fancyFont) {
			if(font == null && !fontErr) {
				try {
					return font = Font.createTrueType(getClass().getResourceAsStream("/Roboto-Regular.ttf"), 16);
				}
				catch(IOException error) {
					fontErr = true;
					logger.error("Could not load HD font", error);
					// else used to fall through to return
				}
			}
			else {
				return font;
			}
		}
		return mc.getFont();
	}

}
