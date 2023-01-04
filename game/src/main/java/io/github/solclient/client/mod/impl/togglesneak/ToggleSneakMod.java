package io.github.solclient.client.mod.impl.togglesneak;

import com.google.gson.annotations.Expose;
import io.github.solclient.client.Client;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.mod.keybinding.ToggleState;

public class ToggleSneakMod extends SimpleHudMod {
	private ToggleState sneak;

	@Expose
	@Option
	private boolean hud;

	private ToggleSneakKeyBinding keybinding;

	@Override
	public void onRegister() {
		super.onRegister();

		Client.INSTANCE.unregisterKeyBinding(mc.gameSettings.keyBindSneak);
		keybinding = new ToggleSneakKeyBinding(this, mc.gameSettings.keyBindSneak.getKeyDescription(), 42,
				mc.gameSettings.keyBindSneak.getKeyCategory());
		mc.gameSettings.keyBindSneak = keybinding;
		Client.INSTANCE.registerKeyBinding(mc.gameSettings.keyBindSneak);
	}

	@Override
	public String getText(boolean editMode) {
		if(!hud) return null;
		if(editMode) return keybinding.getText(editMode);
		return getSneak() == null ? null : keybinding.getText(false);
	}

	@Override
	public String getId() { return "toggle_sneak"; }

	@Override
	public boolean isVisible() { return hud; }

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	public ToggleState getSneak() {
		return sneak;
	}

	public void setSneak(ToggleState sneak) {
		this.sneak = sneak;
	}
}
