package io.github.solclient.client.mod.impl.hud.tablist;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.data.Colour;

public class TabListMod extends SolClientMod {

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
	public void init() {
		super.init();
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
