package io.github.solclient.client.mod.impl.hud.chat;

import net.minecraft.client.resources.I18n;

public enum ChatVisibility {
	SHOWN, COMMANDS, HIDDEN;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.chat.option.visibility." + name().toLowerCase());
	}

}
