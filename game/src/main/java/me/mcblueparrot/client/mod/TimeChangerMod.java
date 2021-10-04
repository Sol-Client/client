package me.mcblueparrot.client.mod;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.TimeEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;

public class TimeChangerMod extends Mod {

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

}
