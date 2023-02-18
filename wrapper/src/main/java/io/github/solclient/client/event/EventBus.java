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

package io.github.solclient.client.event;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.*;

/**
 * Event bus system focused on performance.
 */
public final class EventBus {

	private static final class MethodData {

		public final Object instance;
		public final String name;
		public final MethodHandle target;

		public MethodData(Object instance, Method method) throws IllegalAccessException {
			this.instance = instance;
			name = method.getName();
			target = MethodHandles.lookup().unreflect(method);
		}

	}

	private static final Logger LOGGER = LogManager.getLogger();

	private boolean inPost;
	private final List<Object> toRemove = new LinkedList<>();
	private final Map<Class<?>, List<MethodData>> handlers = new HashMap<Class<?>, List<MethodData>>();

	private Set<Method> getMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<>();

		Collections.addAll(methods, clazz.getMethods());
		Collections.addAll(methods, clazz.getDeclaredMethods());
		// without this, Lookup throws an exception
		methods.forEach((method) -> method.setAccessible(true));

		return methods;
	}

	public void register(Object obj) {
		for (Method method : getMethods(obj.getClass())) {
			if (validate(method)) {
				try {
					handlers.computeIfAbsent(method.getParameters()[0].getType(), (ignore) -> new ArrayList<>())
							.add(new MethodData(obj, method));
				} catch (IllegalAccessException error) {
					LOGGER.error("Could not register " + method.getName(), error);
				}
			}
		}
	}

	public void unregister(Object obj) {
		if (inPost) {
			toRemove.add(obj);
			return;
		}

		for (List<MethodData> methods : handlers.values()) {
			methods.removeIf((method) -> method.instance == obj);
		}
	}

	public <T> T post(T event) {
		if (!handlers.containsKey(event.getClass())) {
			return event;
		}

		try {
			toRemove.clear();
			inPost = true;
			for (MethodData method : handlers.get(event.getClass())) {
				try {
					method.target.invoke(method.instance, event);
				} catch (WrongMethodTypeException error) {
					LOGGER.error("Failed to invoke " + method.name + ":", error);
				} catch (Throwable error) {
					LOGGER.error("Error while executing " + method.name + ":", error);
				}
			}
			inPost = false;
			toRemove.forEach(this::unregister);
		} catch (ConcurrentModificationException ignored) {
		}

		return event;
	}

	private boolean validate(Method method) {
		if (method.isAnnotationPresent(EventHandler.class)) {
			Validate.isTrue(method.getParameterCount() == 1,
					"Method " + method.getName() + " has " + method.getParameterCount() + " parameters; expected 1.");
			return true;
		}
		return false;
	}

}
