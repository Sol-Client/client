package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.ConfigOnlyMod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.font.Font;
import io.github.solclient.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class SolClientMod extends ConfigOnlyMod {

	public static SolClientMod instance;

	@Expose
	@Option
	public boolean fancyMainMenu;

	@Expose
	@Option
	public boolean logoInInventory;

	@Option
	public KeyBinding modsKey = new KeyBinding(getTranslationKey() + ".mods", Keyboard.KEY_RSHIFT, Client.KEY_CATEGORY);

	@Option
	public KeyBinding editHudKey = new KeyBinding(getTranslationKey() + ".edit_hud", 0, Client.KEY_CATEGORY);

	@Expose
	@Option
	public Colour uiColour = new Colour(255, 180, 0);
	public Colour uiHover;

	@Expose
	@Option
	public boolean smoothUIColours = true;

	@Expose
	@Option
	public boolean roundedUI = true;

	@Expose
	@Option
	public boolean buttonClicks = true;

	@Expose
	@Option
	public boolean smoothScrolling = true;

	@Expose
	@Option
	public boolean fancyFont = true;

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
		Client.INSTANCE.registerKeyBinding(modsKey);
		Client.INSTANCE.registerKeyBinding(editHudKey);
		uiHover = getUiHover();
	}

	public static Font getFont() {
		if(instance.fancyFont) {
			return SlickFontRenderer.DEFAULT;
		}
		else {
			return (Font) Minecraft.getMinecraft().fontRendererObj;
		}
	}

	@Override
	public void postOptionChange(String key, Object value) {
		super.postOptionChange(key, value);

		if(key.equals("fancyFont") && mc.currentScreen instanceof ModsScreen) {
			ModsScreen screen = (ModsScreen) mc.currentScreen;
			screen.updateFont();
		}

		if(key.equals("uiColour")) {
			uiHover = getUiHover();
		}
	}

	@Override
	public String getDescription() {
		return super.getDescription();
	}

	private Colour getUiHover() {
		return uiColour.add(40);
	}

}
