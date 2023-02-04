package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.lib.penner.easing.*;
import io.github.solclient.client.ui.component.Component;

public class AnimatedFloatController implements Controller<Float> {

	private final Controller<Float> base;
	private final int duration;
	private Float last;
	private float current;
	private long currentTime;

	public AnimatedFloatController(Controller<Float> base, int duration) {
		this.base = base;
		this.duration = duration;
	}

	@Override
	public Float get(Component component, Float defaultValue) {
		float baseValue = base.get(component, defaultValue);
		if (baseValue != current) {
			if (last != null)
				currentTime = System.currentTimeMillis();
			last = current;
			current = baseValue;
		}

		if (last == null)
			last = current;

		return Sine.easeOut(Math.min(System.currentTimeMillis() - currentTime, duration), last, current - last, duration);
	}

}
