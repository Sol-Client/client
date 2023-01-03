package io.github.solclient.client.mod.impl.togglesprint;

import com.google.gson.annotations.Expose;
import io.github.solclient.client.Client;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.mod.keybinding.ToggleState;

public class ToggleSprintMod extends SimpleHudMod {

	private ToggleState sprint;
	@Expose
	@Option
	private boolean hud;

	private ToggleSprintKeyBinding keybinding;

	@Override
	public void onRegister() {
		super.onRegister();

		Client.INSTANCE.unregisterKeyBinding(mc.gameSettings.keyBindSprint);
		keybinding = new ToggleSprintKeyBinding(this, mc.gameSettings.keyBindSprint.getKeyDescription(), 29,
				mc.gameSettings.keyBindSprint.getKeyCategory());
		mc.gameSettings.keyBindSprint = keybinding;
		Client.INSTANCE.registerKeyBinding(mc.gameSettings.keyBindSprint);
	}

	@Override
	public String getId() {
		return "toggle_sprint";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public boolean isVisible() {
		return hud;
	}

	@Override
	public String getText(boolean editMode) {
		if (!hud) {
			return null;
		}
		if (editMode) {
			return keybinding.getText(true);
		}
		return getSprint() == null ? null : keybinding.getText(false);
	}

	public ToggleState getSprint() {
		return sprint;
	}

	public void setSprint(ToggleState sprint) {
		this.sprint = sprint;
	}

}
