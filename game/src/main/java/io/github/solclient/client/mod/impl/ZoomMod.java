package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.*;
import io.github.solclient.client.event.impl.FovEvent;
import io.github.solclient.client.event.impl.MouseClickEvent;
import io.github.solclient.client.event.impl.ScrollEvent;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

public class ZoomMod extends Mod implements PrimaryIntegerSettingMod {

	public static boolean enabled;
	public static ZoomMod instance;

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey() + ".key", Keyboard.KEY_C, Client.KEY_CATEGORY);
	@Option
	private final KeyBinding zoomOutKey = new KeyBinding(getTranslationKey() + ".zoom_out", Keyboard.KEY_MINUS, Client.KEY_CATEGORY);
	@Option
	private final KeyBinding zoomInKey = new KeyBinding(getTranslationKey() + ".zoom_in", Keyboard.KEY_EQUALS, Client.KEY_CATEGORY);

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
		Client.INSTANCE.registerKeyBinding(key);
		Client.INSTANCE.registerKeyBinding(zoomOutKey);
		Client.INSTANCE.registerKeyBinding(zoomInKey);
	}

	public void start() {
		active = true;
		lastSensitivity = mc.gameSettings.mouseSensitivity;
		resetFactor();
		updateSensitivity();
		wasCinematic = this.mc.gameSettings.smoothCamera;
		mc.gameSettings.smoothCamera = cinematic;
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}

	public void stop() {
		active = false;
		setFactor(1);
		mc.gameSettings.mouseSensitivity = lastSensitivity;
		mc.gameSettings.smoothCamera = wasCinematic;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(key.isKeyDown()) {
			if(!active) {
				start();
			}
		}
		else if(active) {
			stop();
		}
		if(active) {
			if(zoomOutKey.isKeyDown()) {
				zoomOut();
			}
			else if(zoomInKey.isKeyDown()) {
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
			float calculatedAnimatedFactor = lastAnimatedFactor + (animatedFactor - lastAnimatedFactor) * event.partialTicks;

			if(calculatedAnimatedFactor != lastCalculatedAnimatedFactor) {
				mc.renderGlobal.setDisplayListEntitiesDirty();
			}

			lastCalculatedAnimatedFactor = calculatedAnimatedFactor;
			event.fov *= calculatedAnimatedFactor;
			return;
		}
		if(!active) {
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
		if(active && scrolling && event.button == 2) {
			event.cancelled = true;
			resetFactor();
		}
	}

	@EventHandler
	public boolean onScroll(ScrollEvent event) {
		if(active && scrolling) {
			event.cancelled = true;
			if(MouseHandler.isButtonDown(2)) {
				return true;
			}
			if(event.amount < 0) {
				zoomOut();
			}
			else if(event.amount > 0) {
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
			mc.renderGlobal.setDisplayListEntitiesDirty();
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
		return MathHelper.clamp_float(factor, 0, 0.5F);
	}

	public void updateSensitivity() {
		if(reduceSensitivity) {
			mc.gameSettings.mouseSensitivity = lastSensitivity * currentFactor;
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
