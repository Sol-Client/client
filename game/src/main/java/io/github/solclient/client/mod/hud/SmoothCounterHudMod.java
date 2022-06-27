package io.github.solclient.client.mod.hud;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;

public abstract class SmoothCounterHudMod extends SimpleHudMod {

	public abstract int getIntValue();

	public abstract String getSuffix();

	private int counter;

	@EventHandler
	public void onTick(PostTickEvent event) {
		int actualValue = getIntValue();
		if(actualValue > counter) {
			counter += Math.max(((actualValue - counter) / 2), 1);
		}
		else if(actualValue < counter) {
			counter -= Math.max(((counter - actualValue) / 2), 1);
		}
	}

	@Override
	public String getText(boolean editMode) {
		if(editMode) {
			return "0 " + getSuffix();
		}
		else {
			return counter + " " + getSuffix();
		}
	}

}
