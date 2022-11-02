package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.*;
import io.github.solclient.client.event.impl.world.GammaEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;

public class TweaksMod extends Mod {

	public static final TweaksMod INSTANCE = new TweaksMod();

	@Expose
	@Option
	public boolean fullbright;
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
	@Expose
	@Option
	public boolean centredInventory = true;
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
	protected void onEnable() {
		super.onEnable();
		if(borderlessFullscreen && mc.isFullscreen()) {
			setBorderlessFullscreen(true);
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		if(borderlessFullscreen && mc.isFullscreen()) {
			setBorderlessFullscreen(false);
			mc.toggleFullscreenState();
			mc.toggleFullscreenState();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if(isEnabled() && key.equals("borderlessFullscreen")) {
			if(mc.isFullscreen()) {
				if((boolean) value) {
					setBorderlessFullscreen(true);
				}
				else {
					setBorderlessFullscreen(false);
					mc.toggleFullscreenState();
					mc.toggleFullscreenState();
				}
			}
		}
	}

	@EventHandler
	public void onGamma(GammaEvent event) {
		if(fullbright) {
			event.setGamma(20);
		}
	}

	@EventHandler
	public void onFullscreenToggle(FullscreenToggleEvent event) {
		if(borderlessFullscreen) {
			event.setApplyState(false);
			setBorderlessFullscreen(event.getState());
		}
	}

	@EventHandler
	public void onRender(PreRenderEvent event) {
		if(fullscreenTime != -1
				&& System.currentTimeMillis() - fullscreenTime >= 100) {
			fullscreenTime = -1;
			if(!mc.isInMenu()) {
				mc.getMouseHandler().grabCursor();
			}
		}
	}

	public float getDamageShakeIntensity() {
		return damageShakeIntensity / 100;
	}

	// controlled by impl, since LWJGL versions vary so much
	private static void setBorderlessFullscreen(boolean state) {
		throw new UnsupportedOperationException();
	}

}
