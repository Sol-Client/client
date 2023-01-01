package io.github.solclient.client.mod.impl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.util.access.AccessMinecraft;
import io.github.solclient.client.util.data.Rectangle;

public class TweaksMod extends Mod {

	public static boolean enabled;
	public static TweaksMod instance;

	@Expose
	@Option
	private boolean fullbright;
	@Expose
	@Option
	public boolean showOwnTag;
	@Expose
	@Option
	public boolean arabicNumerals;
	@Expose
	@Option
	public boolean betterTooltips = true;
	@Expose
	@Option
	public boolean minimalViewBobbing;
	@Expose
	@Option
	public boolean minimalDamageShake;
	@Expose
	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	private float damageShakeIntensity = 100;
	@Expose
	@Option
	@Slider(min = 0, max = 0.5F, step = 0.01F)
	public float lowerFireBy;
	@Expose
	@Option
	public boolean confirmDisconnect;
	@Expose
	@Option
	public boolean betterKeyBindings = true;
	@Expose
	@Option
	public boolean disableHotbarScrolling;
	@Expose
	@Option
	private boolean borderlessFullscreen;
	@Expose
	@Option
	public boolean centredInventory = true;
	private Rectangle previousBounds;
	private long fullscreenTime = -1;

	@Override
	public String getId() {
		return "tweaks";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.GENERAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
		if (borderlessFullscreen && mc.isFullScreen()) {
			setBorderlessFullscreen(true);
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		if (borderlessFullscreen && mc.isFullScreen()) {
			setBorderlessFullscreen(false);
			mc.toggleFullscreen();
			mc.toggleFullscreen();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if (isEnabled() && key.equals("borderlessFullscreen")) {
			if (mc.isFullScreen()) {
				if ((boolean) value) {
					setBorderlessFullscreen(true);
				} else {
					setBorderlessFullscreen(false);
					mc.toggleFullscreen();
					mc.toggleFullscreen();
				}
			}
		}
	}

	@EventHandler
	public void onGamma(GammaEvent event) {
		if (fullbright) {
			event.gamma = 20F;
		}
	}

	@EventHandler
	public void onFullscreenToggle(FullscreenToggleEvent event) {
		if (borderlessFullscreen) {
			event.applyState = false;
			setBorderlessFullscreen(event.state);
		}
	}

	@EventHandler
	public void onRender(PreRenderTickEvent event) {
		if (fullscreenTime != -1 && System.currentTimeMillis() - fullscreenTime >= 100) {
			fullscreenTime = -1;
			if (mc.inGameHasFocus) {
				mc.mouseHelper.grabMouseCursor();
			}
		}
	}

	public float getDamageShakeIntensity() {
		return damageShakeIntensity / 100;
	}

	private void setBorderlessFullscreen(boolean state) {
		try {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(state));
			Display.setFullscreen(false);
			Display.setResizable(!state);

			if (state) {
				previousBounds = new Rectangle(Display.getX(), Display.getY(), mc.displayWidth, mc.displayHeight);

				Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight()));
				Display.setLocation(0, 0);
				AccessMinecraft.getInstance().resizeWindow(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight());
			} else {
				Display.setDisplayMode(new DisplayMode(previousBounds.getWidth(), previousBounds.getHeight()));
				Display.setLocation(previousBounds.getX(), previousBounds.getY());
				AccessMinecraft.getInstance().resizeWindow(previousBounds.getWidth(), previousBounds.getHeight());

				if (mc.inGameHasFocus) {
					mc.mouseHelper.ungrabMouseCursor();
					fullscreenTime = System.currentTimeMillis();
				}
			}
		} catch (LWJGLException error) {
			logger.error("Could not totggle borderless fullscreen", error);
		}
	}

}
