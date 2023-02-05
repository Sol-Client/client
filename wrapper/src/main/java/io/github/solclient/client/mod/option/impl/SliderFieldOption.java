/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
