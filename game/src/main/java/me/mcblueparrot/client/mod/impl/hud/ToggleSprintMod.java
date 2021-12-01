package me.mcblueparrot.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSprintMod extends SimpleHudMod {

	private SprintState sprint;
	@Expose
	@ConfigOption("HUD")
	private boolean hud;

	public ToggleSprintMod() {
		super("Toggle Sprint", "toggle_sprint", "Toggle the sprint key.");
		category = ModCategory.UTILITY;
	}

	@Override
	public void postStart() {
		super.postStart();
		mc.gameSettings.keyBindSprint = new SprintKeyBind("key.sprint", 29,
				"key.categories.movement");
	}

	@Override
	public boolean isVisible() {
		return hud;
	}

	@Override
	public Rectangle getBounds(Position position) {
		return super.getBounds(position);
	}

	@Override
	public String getText(boolean editMode) {
		if(!hud) {
			return null;
		}
		if(editMode) {
			return "Toggled";
		}
		return getSprint() == null ? null : getSprint().toString();
	}

	public SprintState getSprint() {
		return sprint;
	}

	public void setSprint(SprintState sprint) {
		this.sprint = sprint;
	}

	public enum SprintState {
		HELD("Held"),
		TOGGLED("Toggled");

		private String name;

		SprintState(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public class SprintKeyBind extends KeyBinding {

		private boolean wasDown;
		private long startTime;

		public SprintKeyBind(String description, int keyCode, String category) {
			super(description, keyCode, category);
			Client.INSTANCE.bus.register(this);
		}

		@EventHandler
		public void tickBinding(PostTickEvent event) {
			boolean down = super.isKeyDown();
			if(isEnabled()) {
				if(down) {
					if(!wasDown) {
						startTime = System.currentTimeMillis();
						if(getSprint() == SprintState.TOGGLED) {
							setSprint(SprintState.HELD);
						}
						else {
							setSprint(SprintState.TOGGLED);
						}
					}
					else if((System.currentTimeMillis() - startTime) > 250) {
						setSprint(SprintState.HELD);
					}
				}
				else if(getSprint() == SprintState.HELD) {
					setSprint(null);
				}

				wasDown = down;
			}
		}

		@Override
		public boolean isKeyDown() {
			if(isEnabled()) {
				return mc.currentScreen == null && getSprint() != null;
			}
			return super.isKeyDown();
		}

	}
}
