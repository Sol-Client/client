package io.github.solclient.client.mod.option.impl;

import java.lang.reflect.Field;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.SliderOption;
import io.github.solclient.client.mod.option.annotation.Slider;

public class SliderFieldOption extends FieldOption<Float> implements SliderOption {

	private final Slider annotation;

	public SliderFieldOption(Mod owner, Field field) throws IllegalAccessException {
		super(owner, field);
		annotation = field.getAnnotation(Slider.class);
		if (annotation == null)
			throw new IllegalArgumentException(field + " is not annotated with @Slider");
	}

	@Override
	public boolean shouldShowValue() {
		return annotation.showValue();
	}

	@Override
	public String getFormat() {
		return annotation.format();
	}

	@Override
	public float getMin() {
		return annotation.min();
	}

	@Override
	public float getMax() {
		return annotation.max();
	}

	@Override
	public float getStep() {
		return annotation.step();
	}

}
