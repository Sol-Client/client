package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import gg.essential.universal.USound;
import me.mcblueparrot.client.mod.ConfigOnlyMod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;

public class SolClientMod extends ConfigOnlyMod {

	public static SolClientMod instance;

	@Expose
	@ConfigOption("UI Colour")
	public Colour uiColour = new Colour(255, 180, 0);
	public Colour uiHover;

	@Expose
	@ConfigOption("Fancy Font")
	public boolean fancyFont = true;

	@Expose
	@ConfigOption("Show Logo in Inventory")
	public boolean logoInInventory;

	public SolClientMod() {
		super("Sol Client", "sol_client", "Global settings for Sol Client.", ModCategory.NONE);
		instance = this;
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
		if(!instance.fancyFont) {
			return "Settings for Sol Client, an easy to use Minecraft client.";
		}
		return super.getDescription();
	}

	private Colour getUiHover() {
		return uiColour.add(100);
	}

}
