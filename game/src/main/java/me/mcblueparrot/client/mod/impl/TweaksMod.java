package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;

public class TweaksMod extends Mod {

	public static TweaksMod instance;
	public static boolean enabled;
	@Expose
	@ConfigOption("Void height fix")
	public boolean voidHeightFix = true;
	@Expose
	@ConfigOption("Remove blue void")
	public boolean blueVoidFix = true;
	@Expose
	@ConfigOption("Centred inventory")
	public boolean centredInventory = true;
	@Expose
	@ConfigOption("Show own tag")
	public boolean showOwnTag = true;
	@Expose
	@ConfigOption("Roman to Arabic numerals (0 - 9)")
	public boolean arabicNumberals = true;
	@Expose
	@ConfigOption("Better item tooltips")
	public boolean betterItemTooltip = true;
	@Expose
	@ConfigOption("Ping in tab list")
	public boolean pingInTab = true;

	public TweaksMod() {
		super("Tweaks", "tweaks", "Various game tweaks.", ModCategory.UTILITY);
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
