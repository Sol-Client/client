package me.mcblueparrot.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.*;
import me.mcblueparrot.client.event.impl.CameraRotateEvent;
import me.mcblueparrot.client.event.impl.PlayerHeadRotateEvent;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class PerspectiveMod extends Mod {

	private KeyBinding key = new KeyBinding("Freelook", Keyboard.KEY_V, "Sol Client");
	private float yaw;
	private float pitch;
	private int previousPerspective;
	private boolean active;
	@Expose
	@ConfigOption("Perspective")
	private Perspective perspective = Perspective.THIRD_PERSON_BACK;
	@Expose
	@ConfigOption("Vertically Invert")
	private boolean invertPitch;
	@Expose
	@ConfigOption("Horizontally Invert")
	private boolean invertYaw;

	public PerspectiveMod() {
		super("Freelook", "perspective", "Unlock the camera rotation.", ModCategory.UTILITY);
		Client.INSTANCE.registerKeyBinding(key);
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(key.isKeyDown()) {
			if(!hasStarted()) {
				start();
			}
		}
		else {
			if(hasStarted()) {
				stop();
			}
		}
	}

	public boolean hasStarted() {
		return active;
	}

	public void start() {
		active = true;
		previousPerspective = mc.gameSettings.thirdPersonView;
		mc.gameSettings.thirdPersonView = perspective.ordinal();
		Entity renderView = mc.getRenderViewEntity();
		yaw = renderView.rotationYaw;
		pitch = renderView.rotationPitch;
	}

	public void stop() {
		active = false;
		mc.gameSettings.thirdPersonView = previousPerspective;
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	@EventHandler
	public void setAngles(CameraRotateEvent event) {
		if(active) {
			event.yaw = yaw;
			event.pitch = pitch;
		}
	}

	@EventHandler
	public void setAngles(PlayerHeadRotateEvent event) {
		if(active) {
			float yaw = event.yaw;
			float pitch = event.pitch;
			event.cancelled = true;
			if(!invertPitch) pitch = -pitch;
			if(invertYaw) yaw = -yaw;
			this.yaw += yaw * 0.15F;
			this.pitch = MathHelper.clamp_float(this.pitch + (pitch * 0.15F), -90, 90);
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	public enum Perspective {
		FIRST_PERSON("First"),
		THIRD_PERSON_BACK("Third Back"),
		THIRD_PERSON_FRONT("Third Front");

		private String name;

		private Perspective(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
