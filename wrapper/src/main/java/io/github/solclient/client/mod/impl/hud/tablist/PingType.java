package io.github.solclient.client.mod.impl.hud.tablist;

import net.minecraft.client.resources.I18n;

public enum PingType {
	NONE, ICON, NUMERAL;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.tab_list.option.pingType." + name().toLowerCase());
	}

}
