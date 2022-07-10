package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.input.MouseDownEvent;
import io.github.solclient.client.event.impl.input.ScrollWheelEvent;
import io.github.solclient.client.event.impl.world.FovEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.util.Utils;

public class ZoomMod extends Mod implements PrimaryIntegerSettingMod {

	public static boolean enabled;
	public static ZoomMod instance;

	@Option
	private final KeyBinding key = KeyBinding.create(getTranslationKey() + ".key", Input.C, Client.KEY_CATEGORY),
			zoomOutKey = KeyBinding.create(getTranslationKey() + ".zoom_out", Input.MINUS, Client.KEY_CATEGORY),
			zoomInKey = KeyBinding.create(getTranslationKey() + ".zoom_in", Input.EQUAL, Client.KEY_CATEGORY);

	@Expose
	@Option
	private boolean cinematic = true;
	@Expose
	@Option
	private boolean reduceSensitivity = false;
	@Expose
	@Option
	public boolean scrolling = true;
	@Expose
	@Option
	private boolean smooth = true;
	@Expose
	@Option
	@Slider(min = 2, max = 32, step = 1, format = "sol_client.slider.factor")

	private float factor = 4;
	private float currentFactor = 1;
	private float lastAnimatedFactor = 1;
	private float animatedFactor = 1;
	private float lastCalculatedAnimatedFactor = 1;
	public float lastSensitivity;
	public boolean wasCinematic;
	public boolean active;

	@Override
	public String getId() {
		return "zoom";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		mc.getOptions().addKey(key);
		mc.getOptions().addKey(zoomOutKey);
		mc.getOptions().addKey(zoomInKey);
	}

	public void start() {
		active = true;
		lastSensitivity = mc.getOptions().mouseSensitivity();
		resetFactor();
		updateSensitivity();
		wasCinematic = mc.getOptions().smoothCamera();
		mc.getOptions().setSmoothCamera(cinematic);
		mc.getLevelRenderer().scheduleUpdate();
	}

	public void stop() {
		active = false;
		setFactor(1);
		mc.getOptions().setMouseSensitivity(lastSensitivity);
		mc.getOptions().setSmoothCamera(wasCinematic);
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(key.isHeld()) {
			if(!active) {
				start();
			}
		}
		else if(active) {
			stop();
		}
		if(active) {
			if(zoomOutKey.isHeld()) {
				zoomOut();
			}
			else if(zoomInKey.isHeld()) {
				zoomIn();
			}
		}
	}

	@EventHandler
	public void postTick(PostTickEvent event) {
		if(smooth) {
			lastAnimatedFactor = animatedFactor;

			float multiplier = 0.75F;
			animatedFactor += (currentFactor - animatedFactor) * multiplier;
		}
	}

	@EventHandler
	public void onFov(FovEvent event) {
		if(smooth) {
			float calculatedAnimatedFactor = lastAnimatedFactor + (animatedFactor - lastAnimatedFactor) * event.getTickDelta();

			if(calculatedAnimatedFactor != lastCalculatedAnimatedFactor) {
				mc.getLevelRenderer().scheduleUpdate();
			}

			lastCalculatedAnimatedFactor = calculatedAnimatedFactor;
			event.setFov(event.getFov() * calculatedAnimatedFactor);
			return;
		}
		if(!active) {
			return;
		}
		event.setFov(event.getFov() * currentFactor);
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onMouseClick(MouseDownEvent event) {
		if(active && scrolling && event.getButton() == 2) {
			event.cancel();
			resetFactor();
		}
	}

	@EventHandler
	public boolean onScroll(ScrollWheelEvent event) {
		if(active && scrolling) {
			event.cancel();
			if(Input.isMouseButtonHeld(2)) {
				return true;
			}
			if(event.getAmount() < 0) {
				zoomOut();
			}
			else if(event.getAmount() > 0) {
				zoomIn();
			}
		}
		return true;
	}

	public void zoomOut() {
		zoom(false);
	}

	public void zoomIn() {
		zoom(true);
	}

	public void resetFactor() {
		setFactor(1 / factor);
	}

	public void setFactor(float factor) {
		if(factor != currentFactor) {
			mc.getLevelRenderer().scheduleUpdate();
			updateSensitivity();
		}
		currentFactor = factor;
	}

	public void zoom(boolean in) {
		float changedFactor;
		float divFactor = 1 / currentFactor;
		if(in) {
			changedFactor = divFactor + 1;
		}
		else {
			changedFactor = divFactor - 1;
		}

		setFactor(clamp(1 / changedFactor));
	}


	public float clamp(float factor) {
		return Utils.clamp(factor, 0, 0.5F);
	}

	public void updateSensitivity() {
		if(reduceSensitivity) {
			mc.getOptions().setMouseSensitivity(lastSensitivity * currentFactor);
		}
	}

	@Override
	public void decrement() {
		factor = Math.max(2, factor - 1);
	}

	@Override
	public void increment() {
		factor = Math.min(32, factor + 1);
	}

}
