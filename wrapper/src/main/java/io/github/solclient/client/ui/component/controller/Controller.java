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
