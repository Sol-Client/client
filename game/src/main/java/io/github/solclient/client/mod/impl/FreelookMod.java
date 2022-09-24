package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Constants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.input.CameraRotateEvent;
import io.github.solclient.client.event.impl.world.CameraTransformEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.util.Perspective;
import io.github.solclient.client.util.Utils;

public class FreelookMod extends Mod {

	public static final FreelookMod INSTANCE = new FreelookMod();

	@Option
	private final KeyBinding key = KeyBinding.create(getTranslationKey() + ".key", Input.V, Constants.KEY_CATEGORY);
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

	@Override
	public void onRegister() {
		super.onRegister();
		mc.getOptions().addKey(key);
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(key.isHeld()) {
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
		previousPerspective = mc.getOptions().ordinalPerspective();
		mc.getOptions().setOrdinalPerspective(perspective.ordinal());
		Entity camera = mc.getCameraEntity();
		yaw = camera.yaw();
		pitch = camera.pitch();
	}

	public void stop() {
		active = false;
		mc.getOptions().setOrdinalPerspective(previousPerspective);
		mc.getLevelRenderer().scheduleUpdate();
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	@EventHandler
	public void onCameraTransform(CameraTransformEvent event) {
		if(active) {
			event.setYaw(yaw);
			event.setPitch(pitch);
		}
	}

	@EventHandler
	public void onCameraRotate(CameraRotateEvent event) {
		if(active) {
			float yaw = event.getYaw();
			float pitch = event.getPitch();
			event.cancel();

			if(invertPitch) {
				pitch = -pitch;
			}
			if(invertYaw) {
				yaw = -yaw;
			}

			this.yaw += yaw * 0.15F;
			this.pitch = Utils.clamp(this.pitch + (pitch * 0.15F), -90, 90);

			mc.getLevelRenderer().scheduleUpdate();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
