package me.mcblueparrot.client.mod.impl;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.*;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Perspective;
import net.minecraft.client.settings.KeyBinding;

public class TaplookMod extends Mod {

	@ConfigOption("Key")
	private KeyBinding key = new KeyBinding("Taplook", 0, "Sol Client");
	private int previousPerspective;
	private boolean active;
	@Expose
	@ConfigOption("Perspective")
	private Perspective perspective = Perspective.THIRD_PERSON_BACK;

	public TaplookMod() {
		super("Taplook", "taplook", "Change your perspective by holding a key.", ModCategory.UTILITY);
		Client.INSTANCE.registerKeyBinding(key);
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
