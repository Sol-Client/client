package io.github.solclient.client.mod.impl.togglesneak;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.keybinding.ToggleState;
import io.github.solclient.client.mod.keybinding.ToggledKeyBinding;

public class ToggleSneakKeyBinding extends ToggledKeyBinding<ToggleSneakMod> {
	public ToggleSneakKeyBinding(ToggleSneakMod mod, String description, int keyCode, String category) {
		super(mod, description, keyCode, category);
		Client.INSTANCE.bus.register(this);
	}

	@Override
	public void postStateUpdate(ToggleState newState) {
		this.mod.setSneak(newState);
	}

	@Override
	public ToggleState getState() {
		return this.mod.getSneak();
	}
}
