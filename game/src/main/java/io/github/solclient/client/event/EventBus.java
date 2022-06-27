package io.github.solclient.client.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.AllArgsConstructor;

/**
 * Event bus system focused on performance.
 */
public class EventBus {

	@AllArgsConstructor
	private static class MethodData {

		public final Object instance;
		public final Method target;

	}

	private boolean inPost;
	private final List<Object> toRemove = new ArrayList<>();
	private final Map<Class<?>, List<MethodData>> handlers = new HashMap<Class<?>, List<MethodData>>();
	private final Logger logger = LogManager.getLogger();

	private Set<Method> getMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<>();

		Collections.addAll(methods, clazz.getMethods());
		Collections.addAll(methods, clazz.getDeclaredMethods());

		methods.forEach((method) -> method.setAccessible(true));

		return methods;
	}

	public void register(Object obj) {
		for(Method method : getMethods(obj.getClass())) {
			if(validate(method)) {
				handlers.computeIfAbsent(method.getParameters()[0].getType(), (ignore) -> new ArrayList<>())
						.add(new MethodData(obj, method));
			}
		}
	}

	public void unregister(Object obj) {
		if(inPost) {
			toRemove.add(obj);
			return;
		}

		for(List<MethodData> methods : handlers.values()) {
			methods.removeIf((method) -> method.instance == obj);
		}
	}

	public <T> T post(T event) {
		if(!handlers.containsKey(event.getClass())) {
			return event;
		}

		try {
			toRemove.clear();
			inPost = true;
			for(MethodData method : handlers.get(event.getClass())) {
				try {
					method.target.invoke(method.instance, event);
				}
				catch(IllegalAccessException | IllegalArgumentException error) {
					logger.error("Failed to invoke " + method.target.getName() + ":", error);
				}
				catch(InvocationTargetException error) {
					logger.error("Error while executing " + method.target.getName() + ":", error.getCause());
				}
			}
			inPost = false;
			toRemove.forEach(this::unregister);
		}
		catch(ConcurrentModificationException ignored) {
		}

		return event;
	}

	private boolean validate(Method method) {
		if(method.isAnnotationPresent(EventHandler.class)) {
			Validate.isTrue(method.getParameterCount() == 1, "Method " + method.getName() + " has " + method.getParameterCount() + " parameters; expected 1.");
			return true;
		}
		return false;
	}

}
