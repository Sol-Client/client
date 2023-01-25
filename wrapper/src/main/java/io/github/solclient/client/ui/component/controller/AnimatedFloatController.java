package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;

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

		return MinecraftUtils.lerp(last, current, Math.min(Math.max(0, (System.currentTimeMillis() - currentTime) / ((float) duration)), 1));
	}

}
