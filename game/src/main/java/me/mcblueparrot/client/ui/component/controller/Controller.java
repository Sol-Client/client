package me.mcblueparrot.client.ui.component.controller;

import me.mcblueparrot.client.ui.component.Component;

@FunctionalInterface
public interface Controller<T> {

	T get(Component component, T defaultValue);

}
