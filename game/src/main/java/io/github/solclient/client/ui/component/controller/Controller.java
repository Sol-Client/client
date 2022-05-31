package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.ui.component.Component;

@FunctionalInterface
public interface Controller<T> {

	T get(Component component, T defaultValue);

}
