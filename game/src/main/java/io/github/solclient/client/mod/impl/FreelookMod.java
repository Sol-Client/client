package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.Perspective;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class FreelookMod extends Mod {

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey() + ".key", Keyboard.KEY_V,
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
		if (key.isKeyDown()) {
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
			this.pitch = MathHelper.clamp_float(this.pitch + (pitch * 0.15F), -90, 90);
			mc.renderGlobal.setDisplayListEntitiesDirty();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
