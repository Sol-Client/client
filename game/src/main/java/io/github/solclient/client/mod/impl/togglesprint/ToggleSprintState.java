package io.github.solclient.client.mod.impl.togglesprint;

import net.minecraft.client.resources.I18n;

public enum ToggleSprintState {
	HELD, TOGGLED;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.toggle_sprint." + name().toLowerCase());
	}

}
