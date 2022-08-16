package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Constants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.util.Perspective;

public class TaplookMod extends Mod {

	@Option
	private final KeyBinding key = KeyBinding.create(getTranslationKey() + ".key", Input.NONE, Constants.KEY_CATEGORY);
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

	@Override
	public void onRegister() {
		super.onRegister();
		mc.getOptions().addKey(key);
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
	}

	public void start() {
		active = true;
		previousPerspective = mc.getOptions().ordinalPerspective();
		mc.getOptions().setOrdinalPerspective(perspective.ordinal());
		mc.getLevelRenderer().scheduleUpdate();
	}

	public void stop() {
		active = false;
		mc.getOptions().setOrdinalPerspective(previousPerspective);
		mc.getLevelRenderer().scheduleUpdate();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
