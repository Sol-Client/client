package io.github.solclient.client.mod.impl.hud.tablist;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.data.Colour;
import lombok.AllArgsConstructor;

public class TabListMod extends Mod {

	public static boolean enabled;
	public static TabListMod instance;

	@Expose
	@Option
	public boolean hideHeader;
	@Expose
	@Option
	public boolean hideFooter;
	@Expose
	@Option
	public PingType pingType = PingType.NUMERAL;
	@Expose
	@Option
	public Colour backgroundColour = new Colour(Integer.MIN_VALUE);
	@Expose
	@Option
	public Colour entryBackgroundColour = new Colour(553648127);
	@Expose
	@Option
	public boolean hidePlayerHeads;
	@Expose
	@Option
	public boolean textShadow = true;

	@Override
	public String getId() {
		return "tab_list";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
	}

	@Override
	public void onRegister() {
		super.onRegister();
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

}
