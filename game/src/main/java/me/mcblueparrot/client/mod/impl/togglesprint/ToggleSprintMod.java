package me.mcblueparrot.client.mod.impl.togglesprint;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSprintMod extends SimpleHudMod {

	private ToggleSprintState sprint;
	@Expose
	@Option
	private boolean hud;

	@Override
	public void onRegister() {
		super.onRegister();

		Client.INSTANCE.unregisterKeyBinding(mc.gameSettings.keyBindSprint);
		mc.gameSettings.keyBindSprint = new ToggleSprintKeyBinding(this, mc.gameSettings.keyBindSprint.getKeyDescription(), 29,
				mc.gameSettings.keyBindSprint.getKeyCategory());
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
		if(!hud) {
			return null;
		}
		if(editMode) {
			return ToggleSprintState.TOGGLED.toString();
		}
		return getSprint() == null ? null : getSprint().toString();
	}

	public ToggleSprintState getSprint() {
		return sprint;
	}

	public void setSprint(ToggleSprintState sprint) {
		this.sprint = sprint;
	}

}
