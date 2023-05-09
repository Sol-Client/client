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

package io.github.solclient.client.util;

import com.google.common.base.*;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CachedFunction<O> {

	private final Supplier<Object> controlValueSupplier;
	private final Function<Object, O> mapper;
	private Object lastControlValue;
	private O lastOutput;

	public static <I, O> CachedFunction<O> withComparison(Supplier<I> controlValueSupplier, Function<I, O> mapper) {
		return new CachedFunction<O>((Supplier) controlValueSupplier, (Function) mapper);
	}

	public static <O> CachedFunction<O> withHashCode(Object controlValue, Supplier<O> supplier) {
		return new CachedFunction<>(() -> controlValue.hashCode(), ignored -> supplier.get());
	}

	public O get() {
		Object controlValue = controlValueSupplier.get();

		if (!controlValue.equals(lastControlValue)) {
			lastControlValue = controlValue;
			return lastOutput = mapper.apply(lastControlValue);
		}

		return lastOutput;
	}
}
