package me.mcblueparrot.client.mod.impl.hud;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import net.minecraft.client.Minecraft;

public class FpsMod extends SimpleHudMod {

	public int counter;

	public FpsMod() {
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
