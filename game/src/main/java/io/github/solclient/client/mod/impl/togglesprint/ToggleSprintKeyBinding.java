package io.github.solclient.client.mod.impl.togglesprint;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.mod.keybinding.ToggleState;
import io.github.solclient.client.mod.keybinding.ToggledKeyBinding;
import net.minecraft.client.Minecraft;

public class ToggleSprintKeyBinding extends ToggledKeyBinding<ToggleSprintMod> {
	public ToggleSprintKeyBinding(ToggleSprintMod mod, String description, int keyCode, String category) {
		super(mod, description, keyCode, category);
		Client.INSTANCE.bus.register(this);
	}

	@Override
	public void postStateUpdate(ToggleState newState) {
		this.mod.setSprint(newState);
	}

	@Override
	public ToggleState getState() {
		return this.mod.getSprint();
	}
}