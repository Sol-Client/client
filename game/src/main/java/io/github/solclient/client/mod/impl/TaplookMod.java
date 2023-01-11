package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.Perspective;
import net.minecraft.client.settings.KeyBinding;

public class TaplookMod extends Mod {

	@Option
	private final KeyBinding key = new KeyBinding(getTranslationKey() + ".key", 0, GlobalConstants.KEY_CATEGORY);
	private int previousPerspective;
	private boolean active;
	@Expose
	@Option
	private Perspective perspective = Perspective.THIRD_PERSON_BACK;

	@Override
	public String getId() {
		return "taplook";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (key.isKeyDown()) {
			if (!active) {
				start();
			}
		} else if (active) {
			stop();
		}
	}

	public void start() {
		active = true;
		previousPerspective = mc.gameSettings.thirdPersonView;
		mc.gameSettings.thirdPersonView = perspective.ordinal();
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}

	public void stop() {
		active = false;
		mc.gameSettings.thirdPersonView = previousPerspective;
		mc.renderGlobal.setDisplayListEntitiesDirty();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
