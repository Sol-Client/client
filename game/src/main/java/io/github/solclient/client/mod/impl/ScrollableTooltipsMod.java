package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import net.minecraft.client.gui.GuiScreen;

public class ScrollableTooltipsMod extends Mod {

	public static ScrollableTooltipsMod instance;
	public static boolean enabled;
	
	@Expose
	@Option
	@Slider(min = 0.5F, max = 5, step = 0.5F)
	private float scrollSensitivity = 1;
	@Expose
	@Option
	private boolean reverse;

	public int offsetX;
	public int offsetY;

	@Override
	public String getId() {
		return "scrollable_tooltips";
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
	
	public void onRenderTooltip() {
		if(!isEnabled()) {
			return;
		}

		int wheel = Mouse.getDWheel();

		if(wheel != 0) {
			onScroll(wheel > 0);
		}
	}

	public void onScroll(boolean direction) {
		int scrollStep = (int) (12 * this.scrollSensitivity);

		if(direction) {
			scrollStep = -scrollStep;
		}
		
		if(!reverse) {
			scrollStep = -scrollStep;
		}

		if(GuiScreen.isShiftKeyDown()) {
			offsetX += scrollStep;
		}
		else {
			offsetY += scrollStep;
		}
	}

	public void resetScroll() {
		offsetX = offsetY = 0;
	}

}
