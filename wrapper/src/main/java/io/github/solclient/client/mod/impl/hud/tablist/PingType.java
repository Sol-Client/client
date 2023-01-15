package io.github.solclient.client.mod.impl.hud.tablist;

import net.minecraft.client.resource.language.I18n;

public enum PingType {
	NONE, ICON, NUMERAL;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.tab_list.option.pingType." + name().toLowerCase());
	}

}
