package io.github.solclient.client.util;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DirtyMapper<I, O> {

	private final Supplier<I> controlValueSupplier;
	private final Function<I, O> mapper;
	private I lastControlValue;
	private O lastOutput;

	public O get() {
		I controlValue = controlValueSupplier.get();

		if(!controlValue.equals(lastControlValue)) {
			lastControlValue = controlValue;
			return lastOutput = mapper.apply(lastControlValue);
		}

		return lastOutput;
	}
}
