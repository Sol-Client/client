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

package io.github.solclient.client.mod.option;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.util.data.*;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resource.language.I18n;

/**
 * Represents a mod option.
 */
@RequiredArgsConstructor
public abstract class ModOption<T> {

	private final String name;
	private final ModOptionStorage<T> storage;

	/**
	 * Gets the option name.
	 *
	 * @return the name key.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the option type.
	 *
	 * @return the type class.
	 */
	public Class<T> getType() {
		return storage.getType();
	}

	/**
	 * Casts the option.
	 *
	 * @param <N>  the new type.
	 * @param type the type class.
	 * @return the (safely) casted option.
	 */
	public <N> ModOption<N> cast(Class<N> type) {
		if (!getType().equals(type))
			throw new ClassCastException();

		return (ModOption<N>) this;
	}

	/**
	 * Unsafely casts the option. If you haven't checked the type, this may cause
	 * problems when you get the value.
	 *
	 * @param <N> the new type.
	 * @return the casted option.
	 */
	public <N> ModOption<N> unsafeCast() {
		return (ModOption<N>) this;
	}

	/**
	 * Gets the option value.
	 *
	 * @return the value.
	 */
	public T getValue() {
		return storage.get();
	}

	/**
	 * Sets the option value.
	 *
	 * @param value the value.
	 */
	public void setValue(T value) {
		storage.set(value);
	}

	/**
	 * Sets the option from another option.
	 *
	 * @param option the option.
	 */
	public void setFrom(ModOption<T> option) {
		storage.setFrom(option);
	}

	protected Component createDefaultComponent() {
		return createDefaultComponent(20);
	}

	protected Component createDefaultComponent(int height) {
		return createDefaultComponent(height, true);
	}

	protected Component createDefaultComponent(int height, boolean label) {
		Component component = new Component() {

			@Override
			public Rectangle getDefaultBounds() {
				return Rectangle.ofDimensions(230, height);
			}

		};
		if (label)
			component.add(new LabelComponent(Controller.of(() -> I18n.translate(name))),
					new AlignedBoundsController(Alignment.START, Alignment.CENTRE));
		return component;
	}

	/**
	 * Creates a component for the option.
	 *
	 * @return the component.
	 */
	public abstract Component createComponent();

	@Override
	public String toString() {
		return "ModOption#" + getName() + '(' + storage + ')';
	}

}
