package me.mcblueparrot.client.ui.component.controller;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.util.data.Colour;

@RequiredArgsConstructor
public class AnimatedController<T> implements Controller<T> {

	private final Controller<T> base;
	private final int duration;
	private T last;
	private long currentTime;
	private T current;

	public AnimatedController(Controller<T> base) {
		this(base, 200);
	}

	@Override
	public T get(Component component, T defaultValue) {
		T baseValue = base.get(component, defaultValue);

		if(!baseValue.equals(current)) {
			last = current;
			current = baseValue;
			currentTime = System.currentTimeMillis();
		}

		return animate(Math.max(0, (System.currentTimeMillis() - currentTime) / ((float) duration)));
	}

	@SuppressWarnings("unchecked")
	private T animate(float progress) {
		if(last instanceof Colour) {
			Colour colour = (Colour) last;
			return (T) colour.blend((Colour) current, progress);
		}

		return current;
	}

}
