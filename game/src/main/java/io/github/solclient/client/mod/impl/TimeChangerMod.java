package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.level.DetermineTimeEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;

public class TimeChangerMod extends Mod implements PrimaryIntegerSettingMod {

	@Expose
	@Option
	@Slider(min = 0, max = 24000, step = 1, showValue = false)
	private float time = 1000;

	@Override
	public String getId() {
		return "time_changer";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onTime(DetermineTimeEvent event) {
		event.setTime((long) time);
	}

	@Override
	public void decrement() {
		time = Math.max(0, time - 1000);
	}

	@Override
	public void increment() {
		time = Math.min(24000, time + 1000);
	}

}
