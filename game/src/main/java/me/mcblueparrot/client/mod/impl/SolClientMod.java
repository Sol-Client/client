package me.mcblueparrot.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.ConfigOnlyMod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class SolClientMod extends ConfigOnlyMod {

	public static SolClientMod instance;

	@Expose
	@ConfigOption("UI Colour")
	public Colour uiColour = new Colour(255, 180, 0);
	public Colour uiHover;

	@Expose
	@ConfigOption("Smooth UI Colours")
	public boolean smoothUIColours = true;

	@Expose
	@ConfigOption("Button Clicks")
	public boolean buttonClicks = true;

	@Expose
	@ConfigOption("Smooth Scrolling")
	public boolean smoothScrolling = true;

	@Expose
	@ConfigOption("Fancy Main Menu")
	public boolean fancyMainMenu;

	@Expose
	@ConfigOption("Fancy Font")
	public boolean fancyFont = true;

	@Expose
	@ConfigOption("Show Logo in Inventory")
	public boolean logoInInventory;

	@ConfigOption("Mods Key")
	public KeyBinding modsKey = new KeyBinding("Mods", Keyboard.KEY_RSHIFT, "Sol Client");
	@ConfigOption("Edit HUD Key")
	public KeyBinding editHudKey = new KeyBinding("Edit HUD", Keyboard.KEY_GRAVE, "Sol Client");

	public SolClientMod() {
		super("Sol Client", "sol_client", "Settings for Sol Client.", ModCategory.NONE);
		instance = this;
		Client.INSTANCE.registerKeyBinding(modsKey);
		Client.INSTANCE.registerKeyBinding(editHudKey);
	}

	@Override
	public void onRegister() {
		super.onRegister();
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
		return uiColour.add(60);
	}

}
