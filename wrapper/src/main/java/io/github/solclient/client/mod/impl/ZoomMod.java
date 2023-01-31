package io.github.solclient.client.mod.impl;

import org.lwjgl.input.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

public class ZoomMod extends SolClientMod implements PrimaryIntegerSettingMod {

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey("key"), Keyboard.KEY_C,
			GlobalConstants.KEY_CATEGORY);
	@Option
	private final KeyBinding zoomOutKey = new KeyBinding(getTranslationKey("zoom_out"), Keyboard.KEY_MINUS,
			GlobalConstants.KEY_CATEGORY);
	@Option
	private final KeyBinding zoomInKey = new KeyBinding(getTranslationKey("zoom_in"), Keyboard.KEY_EQUALS,
			GlobalConstants.KEY_CATEGORY);

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
	@Slider(min = 1, max = 100, step = 1, format = "sol_client.slider.percent")
	private float animationSpeed = 75;
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
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.inspired_by", "EnnuiL");
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	public void start() {
		active = true;
		lastSensitivity = mc.options.sensitivity;
		resetFactor();
		updateSensitivity();
		wasCinematic = mc.options.smoothCameraEnabled;
		mc.options.smoothCameraEnabled = cinematic;
		mc.worldRenderer.scheduleTerrainUpdate();
	}

	public void stop() {
		active = false;
		setFactor(1);
		mc.options.sensitivity = lastSensitivity;
		mc.options.smoothCameraEnabled = wasCinematic;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (key.isPressed()) {
			if (!active)
				start();
		} else if (active)
			stop();

		if (active) {
			if (zoomOutKey.isPressed())
				zoomOut();
			else if (zoomInKey.isPressed())
				zoomIn();
		}
	}

	@EventHandler
	public void postTick(PostTickEvent event) {
		if (smooth) {
			lastAnimatedFactor = animatedFactor;

			animatedFactor += (currentFactor - animatedFactor) * (animationSpeed / 100);
		}
	}

	@EventHandler
	public void onFov(FovEvent event) {
		if (smooth) {
			float calculatedAnimatedFactor = lastAnimatedFactor
					+ (animatedFactor - lastAnimatedFactor) * event.partialTicks;

			if (calculatedAnimatedFactor != lastCalculatedAnimatedFactor) {
				mc.worldRenderer.scheduleTerrainUpdate();
			}

			lastCalculatedAnimatedFactor = calculatedAnimatedFactor;
			event.fov *= calculatedAnimatedFactor;
			return;
		}
		if (!active) {
			return;
		}
		event.fov *= currentFactor;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onMouseClick(MouseClickEvent event) {
		if (active && scrolling && event.button == 2) {
			event.cancelled = true;
			resetFactor();
		}
	}

	@EventHandler
	public boolean onScroll(ScrollEvent event) {
		if (active && scrolling) {
			event.cancelled = true;
			if (Mouse.isButtonDown(2)) {
				return true;
			}
			if (event.amount < 0) {
				zoomOut();
			} else if (event.amount > 0) {
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
		if (factor != currentFactor) {
			mc.worldRenderer.scheduleTerrainUpdate();
			updateSensitivity();
		}
		currentFactor = factor;
	}

	public void zoom(boolean in) {
		float changedFactor;
		float divFactor = 1 / currentFactor;
		if (in) {
			changedFactor = divFactor + 1;
		} else {
			changedFactor = divFactor - 1;
		}

		setFactor(clamp(1 / changedFactor));
	}

	public float clamp(float factor) {
		return MathHelper.clamp(factor, 0, 0.5F);
	}

	public void updateSensitivity() {
		if (reduceSensitivity) {
			mc.options.sensitivity = lastSensitivity * currentFactor;
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
