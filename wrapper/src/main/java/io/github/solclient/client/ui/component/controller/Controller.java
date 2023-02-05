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

package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.ui.component.Component;

@FunctionalInterface
public interface Controller<T> {

	static <T> Controller<T> of(T value) {
		return (component, defaultValue) -> value;
	}

	static <T> Controller<T> none() {
		return (component, defaultValue) -> defaultValue;
	}

	default T get(Component component) {
		return get(component, null);
	}

	T get(Component component, T defaultValue);

}
