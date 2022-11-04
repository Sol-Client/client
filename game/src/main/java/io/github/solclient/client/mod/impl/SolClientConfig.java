package io.github.solclient.client.mod.impl;

import java.io.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Constants;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.TrueTypeFont;
import io.github.solclient.client.util.data.Colour;

public final class SolClientConfig extends ConfigOnlyMod {

	public static final SolClientConfig INSTANCE = new SolClientConfig();

	@Expose
	@Option
	public boolean fancyMainMenu = true, logoInInventory;

	@Option
	public KeyBinding modsKey = KeyBinding.create(getTranslationKey() + ".mods", Input.RIGHT_SHIFT,
			Constants.KEY_CATEGORY),
			editHudKey = KeyBinding.create(getTranslationKey() + ".edit_hud", Input.UNKNOWN, Constants.KEY_CATEGORY);

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

	private TrueTypeFont font;
	private boolean fontError;

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
		if(fancyFont && !fontError) {
			if(font == null) {
				try {
					try(InputStream in = getClass().getResourceAsStream("/Roboto-Regular.ttf")) {
						return font = new TrueTypeFont(in, 16);
					}
				}
				catch(IOException | IllegalStateException error) {
					fontError = true;
					logger.error("Could not load HD font", error);
				}
			}
			else {
				return font;
			}
		}
		if(font != null) {
			try {
				font.close();
			}
			catch(IOException error) {
				logger.error("Could not close font", error);
			}
		}
		font = null;
		return mc.getFont();
	}

}
