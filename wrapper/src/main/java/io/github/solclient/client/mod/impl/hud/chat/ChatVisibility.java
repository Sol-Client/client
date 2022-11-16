package io.github.solclient.client.mod.impl.hud.chat;

import io.github.solclient.client.platform.mc.lang.I18n;

public enum ChatVisibility {
	SHOWN,
	COMMANDS,
	HIDDEN;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.chat.option.visibility." + name().toLowerCase());
	}

}
