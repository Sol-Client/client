package io.github.solclient.client.mod.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.mod.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.annotation.Option;

@AbstractTranslationKey("sol_client.mod.smooth_counter_hud")
public abstract class SmoothCounterHudMod extends SimpleHudMod {

	@Expose
	@Option
	private boolean smoothNumbers = true;

	public abstract int getIntValue();

	public abstract String getSuffix();

	private int counter;

	@EventHandler
	public void onTick(PostTickEvent event) {
		if(!mc.hasLevel()) return;

		int actualValue = getIntValue();

		if(!smoothNumbers) {
			counter = actualValue;
			return;
		}

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
