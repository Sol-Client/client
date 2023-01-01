package io.github.solclient.client.mod.impl.togglesprint;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSprintKeyBinding extends KeyBinding {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final ToggleSprintMod mod;
	private boolean wasDown;
	private long startTime;

	public ToggleSprintKeyBinding(ToggleSprintMod mod, String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.mod = mod;
		Client.INSTANCE.bus.register(this);
	}

	@EventHandler
	public void tickBinding(PostTickEvent event) {
		boolean down = super.isKeyDown();
		if (mod.isEnabled()) {
			if (down) {
				if (!wasDown) {
					startTime = System.currentTimeMillis();
					if (mod.getSprint() == ToggleSprintState.TOGGLED) {
						mod.setSprint(ToggleSprintState.HELD);
					} else {
						mod.setSprint(ToggleSprintState.TOGGLED);
					}
				} else if ((System.currentTimeMillis() - startTime) > 250) {
					mod.setSprint(ToggleSprintState.HELD);
				}
			} else if (mod.getSprint() == ToggleSprintState.HELD) {
				mod.setSprint(null);
			}

			wasDown = down;
		}
	}

	@Override
	public boolean isKeyDown() {
		if (mod.isEnabled()) {
			return mc.currentScreen == null && mod.getSprint() != null;
		}
		return super.isKeyDown();
	}

}