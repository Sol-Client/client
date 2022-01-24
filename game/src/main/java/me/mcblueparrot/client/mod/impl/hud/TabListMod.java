package me.mcblueparrot.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.data.Colour;

public class TabListMod extends Mod {

	public static boolean enabled;
	public static TabListMod instance;
	@Expose
	@ConfigOption("Hide Header")
	public boolean hideHeader;
	@Expose
	@ConfigOption("Hide Footer")
	public boolean hideFooter;
	@Expose
	@ConfigOption("Ping")
	public PingType pingType = PingType.NUMERAL;
	@Expose
	@ConfigOption("List Background Colour")
	public Colour backgroundColour = new Colour(Integer.MIN_VALUE);
	@Expose
	@ConfigOption("Entry Background Colour")
	public Colour entryBackgroundColour = new Colour(553648127);
	@Expose
	@ConfigOption("Hide Player Heads")
	public boolean hidePlayerHeads;
	@Expose
	@ConfigOption("Text Shadow")
	public boolean textShadow = true;

	public TabListMod() {
		super("Tab List", "tab_list", "Customise the tab list.", ModCategory.HUD);
		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@AllArgsConstructor
	public enum PingType {
		NONE("None"),
		ICON("Icon"),
		NUMERAL("Numeral");

		private String name;

		@Override
		public String toString() {
			return name;
		}

	}

}
