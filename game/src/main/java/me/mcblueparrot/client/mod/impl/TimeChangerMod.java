package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.TimeEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;

public class TimeChangerMod extends Mod implements PrimaryIntegerSettingMod {

	@Expose
	@ConfigOption("Time")
	@Slider(min = 0, max = 24000, step = 1, showValue = false)
	public float time = 1000;

	public TimeChangerMod() {
		super("Time Changer", "time_changer", "Change the visual time.", ModCategory.VISUAL);
	}

	@EventHandler
	public void onTime(TimeEvent event) {
		event.time = (long) time;
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
