package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.platform.mc.option.ToggleKeyBinding;

public class ToggleSprintMod extends SimpleHudMod {

	public static final ToggleSprintMod INSTANCE = new ToggleSprintMod();

	private static final String TOGGLED = "sol_client.mod.toggle_sprint.toggled";
	private static final String HELD = "sol_client.mod.toggle_sprint.held";

	private ToggleKeyBinding key;

	@Expose
	@Option
	private boolean hud;

	@Override
	public void onRegister() {
		super.onRegister();
		mc.getOptions().removeKey(mc.getOptions().sprintKey());
		mc.getOptions().overwriteSprintKey(ToggleKeyBinding.create(mc.getOptions().sprintKey().getName(), 29,
				mc.getOptions().sprintKey().getKeyCategory(), this::isEnabled, 250));
		mc.getOptions().addKey(mc.getOptions().sprintKey());
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
		if(!hud) {
			return null;
		}
		if(editMode) {
			return I18n.translate(HELD);
		}
		return key.isHeld() ? (key.realHeld() ? I18n.translate(HELD) : I18n.translate(TOGGLED)) : null;
	}

}
