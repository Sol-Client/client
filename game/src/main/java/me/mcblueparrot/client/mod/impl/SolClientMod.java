package me.mcblueparrot.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.ConfigOnlyMod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
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
	public KeyBinding editHudKey = new KeyBinding(getTranslationKey() + ".edit_hud", Keyboard.KEY_GRAVE, Client.KEY_CATEGORY);

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
		return ModCategory.NONE;
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
