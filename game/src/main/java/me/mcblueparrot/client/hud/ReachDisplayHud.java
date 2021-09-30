package me.mcblueparrot.client.hud;

import java.text.DecimalFormat;

import me.mcblueparrot.client.events.EntityAttackEvent;
import me.mcblueparrot.client.events.EventHandler;

public class ReachDisplayHud extends SimpleHud {

    private double distance = 0;
    private long lastCalculate = -1;
    private long hitTime = -1;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

    public ReachDisplayHud() {
        super("Reach Display", "reach_display", "Display your reach when hitting entities.");
    }

    @Override
    public String getText(boolean editMode) {
        if((System.currentTimeMillis() - hitTime) > 5000) {
            distance = 0;
        }
        if(editMode) {
            return "0 mts";
        }
        else {
            return FORMAT.format(distance) + " m" + (distance != 1.0 ? "ts" : "");
        }
    }

    @EventHandler
    public void deffoNoReachHax(EntityAttackEvent event) {
        distance = Math.sqrt(mc.thePlayer.getDistanceSqToEntity(event.victim));
        hitTime = System.currentTimeMillis();
    }

}
