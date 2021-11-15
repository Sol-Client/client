package me.mcblueparrot.client.mod.impl.hud;

import java.util.ArrayList;
import java.util.List;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.PostTickEvent;
import me.mcblueparrot.client.mod.hud.SimpleHud;
import net.minecraft.client.Minecraft;

public class FpsHud extends SimpleHud {

    public int counter;

    public FpsHud() {
        super("FPS", "fps", "Display the FPS (frames per second).");
    }

    @EventHandler
    public void onTick(PostTickEvent event) {
        int actualFPS = Minecraft.getDebugFPS();
        if(actualFPS > counter) {
            counter += Math.max(((actualFPS - counter) / 2), 1);
        }
        else if(actualFPS < counter) {
            counter -= Math.max(((counter - actualFPS) / 2), 1);
        }
    }

    @Override
    public String getText(boolean editMode) {
        if(editMode) {
            return "0 FPS";
        }
        else {
            return counter + " FPS";
        }
    }

}
