package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GammaEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;

public class TweaksMod extends Mod {

	public static boolean enabled;
	public static TweaksMod instance;

	@Expose
	@ConfigOption("Fullbright")
	public boolean fullbright;
	@Expose
	@ConfigOption("Show Own Tag")
	public boolean showOwnTag;
	@Expose
	@ConfigOption("Arabic Numerals")
	public boolean arabicNumerals;
	@Expose
	@ConfigOption("Better Item Tooltips")
	public boolean betterTooltips = true;
	@Expose
	@ConfigOption("Minimal View Bobbing")
	public boolean minimalViewBobbing;
	@Expose
	@ConfigOption("Confirm Disconnect")
	public boolean confirmDisconnect;
	@Expose
	@ConfigOption("Better Key Bindings")
	public boolean betterKeyBindings = true;

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

	@EventHandler
	public void onGamma(GammaEvent event) {
		if(fullbright) {
			event.gamma = 20F;
		}
	}

	@AllArgsConstructor
	public enum ViewBobbingStyle {
		HAND_AND_WORLD("Hand and World"),
		HAND_ONLY("Hand Only"),
		WORLD_ONLY("World Only");

		private String name;

		@Override
		public String toString() {
			return name;
		}

	}

}
