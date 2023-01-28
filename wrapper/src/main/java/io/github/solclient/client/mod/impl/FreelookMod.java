package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.Perspective;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class FreelookMod extends SolClientMod {

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey("key"), Keyboard.KEY_V,
			GlobalConstants.KEY_CATEGORY);
	private float yaw;
	private float pitch;
	private int previousPerspective;
	private boolean active;
	@Expose
	@Option
	private Perspective perspective = Perspective.THIRD_PERSON_BACK;
	@Expose
	@Option
	private boolean invertPitch;
	@Expose
	@Option
	private boolean invertYaw;

	@Override
	public String getId() {
		return "freelook";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (key.isPressed()) {
			if (!hasStarted()) {
				start();
			}
		} else {
			if (hasStarted()) {
				stop();
			}
		}
	}

	public boolean hasStarted() {
		return active;
	}

	public void start() {
		active = true;
		previousPerspective = mc.options.perspective;
		mc.options.perspective = perspective.ordinal();
		Entity camera = mc.getCameraEntity();
		yaw = camera.yaw;
		pitch = camera.pitch;
	}

	public void stop() {
		active = false;
		mc.options.perspective = previousPerspective;
		mc.worldRenderer.scheduleTerrainUpdate();
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	@EventHandler
	public void setAngles(CameraRotateEvent event) {
		if (active) {
			event.yaw = yaw;
			event.pitch = pitch;
		}
	}

	@EventHandler
	public void setAngles(PlayerHeadRotateEvent event) {
		if (active) {
			float yaw = event.yaw;
			float pitch = event.pitch;
			event.cancelled = true;
			if (!invertPitch)
				pitch = -pitch;
			if (invertYaw)
				yaw = -yaw;
			this.yaw += yaw * 0.15F;
			this.pitch = MathHelper.clamp(this.pitch + (pitch * 0.15F), -90, 90);
			mc.worldRenderer.scheduleTerrainUpdate();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
