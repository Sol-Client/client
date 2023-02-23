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

import java.util.function.Consumer;

import com.google.common.base.Supplier;

public interface ModOptionStorage<T> {

	static <T> ModOptionStorage<T> of(Class<T> type, Supplier<T> getter) {
		return of(type, getter, (ignored) -> {
			throw new UnsupportedOperationException("Option is not settable");
		});
	}

	static <T> ModOptionStorage<T> of(Class<T> type, Supplier<T> getter, Consumer<T> setter) {
		return new ModOptionStorage<T>() {

			@Override
			public T get() {
				return getter.get();
			}

			@Override
			public void set(T value) {
				setter.accept(value);
			}

			@Override
			public Class<T> getType() {
				return type;
			}

		};
	}

	Class<T> getType();

	T get();

	void set(T value);

	default void setFrom(ModOption<T> option) {
		set(option.getValue());
	}

	default <N> ModOptionStorage<N> cast(Class<N> type) {
		if (!getType().equals(type))
			throw new ClassCastException();

		return (ModOptionStorage<N>) this;
	}

	default <N> ModOptionStorage<N> unsafeCast() {
		return (ModOptionStorage<N>) this;
	}

}
