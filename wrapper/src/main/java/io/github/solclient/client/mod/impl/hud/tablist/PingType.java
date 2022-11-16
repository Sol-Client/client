package io.github.solclient.client.mod.impl.hud.tablist;

import io.github.solclient.client.platform.mc.lang.I18n;

public enum PingType {
	NONE,
	ICON,
	NUMERAL;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.tab_list.option.pingType." + name().toLowerCase());
	}

}
