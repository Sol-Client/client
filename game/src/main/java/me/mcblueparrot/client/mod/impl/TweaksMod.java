package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.mod.annotation.Slider;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.FullscreenToggleEvent;
import me.mcblueparrot.client.event.impl.GammaEvent;
import me.mcblueparrot.client.event.impl.PreRenderTickEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import me.mcblueparrot.client.util.data.Rectangle;

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
	private Rectangle previousBounds;
	private long fullscreenTime = -1;

	@Override
	public String getId() {
		return "tweaks";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
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
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if(key.equals("borderlessFullscreen")) {
			if(mc.isFullScreen()) {
				if((boolean) value) {
					setBorderlessFullscreen(true);
				}
				else {
					setBorderlessFullscreen(false);
					mc.toggleFullscreen();
					mc.toggleFullscreen();
				}
			}
		}
	}

	@EventHandler
	public void onGamma(GammaEvent event) {
		if(fullbright) {
			event.gamma = 20F;
		}
	}

	@EventHandler
	public void onFullscreenToggle(FullscreenToggleEvent event) {
		if(borderlessFullscreen) {
			event.applyState = false;
			setBorderlessFullscreen(event.state);
		}
	}

	@EventHandler
	public void onRender(PreRenderTickEvent event) {
		if(fullscreenTime != -1
				&& System.currentTimeMillis() - fullscreenTime >= 100) {
			fullscreenTime = -1;
			if(mc.inGameHasFocus) {
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

			if(state) {
				previousBounds = new Rectangle(Display.getX(), Display.getY(), mc.displayWidth, mc.displayHeight);

				Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
				Display.setLocation(0, 0);
				AccessMinecraft.getInstance().resizeWindow(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
			}
			else {
				Display.setDisplayMode(new DisplayMode(previousBounds.getWidth(), previousBounds.getHeight()));
				Display.setLocation(previousBounds.getX(), previousBounds.getY());
				AccessMinecraft.getInstance().resizeWindow(previousBounds.getWidth(), previousBounds.getHeight());

				if(mc.inGameHasFocus) {
					mc.mouseHelper.ungrabMouseCursor();
					fullscreenTime = System.currentTimeMillis();
				}
			}
		}
		catch(LWJGLException error) {
			logger.error("Could not totggle borderless fullscreen", error);
		}
	}

}
