package io.github.solclient.client.mod.option.impl;

import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

/**
 * ModOption implementation using fields and method handles.
 *
 * @param <T> the option type.
 */
public class FieldOption<T> implements ModOption<T> {

	private static final Lookup LOOKUP = MethodHandles.lookup();

	protected final Mod owner;
	protected final String name;
	private final MethodHandle get;
	private final MethodHandle set;
	private final Option annotation;
	private final String translationKey;

	public static List<ModOption<?>> getFieldOptionsFromClass(Mod instance) {
		return getFields(instance).stream().map((field) -> {
			try {
				return create(instance, field);
			} catch (IllegalAccessException error) {
				throw new AssertionError("failed to access " + field, error);
			}
		}).filter((field) -> !(instance instanceof ConfigOnlyMod && "enabled".equals(field.name)))
				.sorted(Comparator.<ModOption<?>>comparingInt(ModOption::getPriority).reversed())
				.collect(Collectors.toList());
	}

	private static List<Field> getFields(Object instance) {
		List<Field> result = new LinkedList<>();
		Class<?> clazz = instance.getClass();
		while (clazz != null) {
			collectFields(clazz, result);
			clazz = clazz.getSuperclass();
		}

		return result;
	}

	private static void collectFields(Class<?> clazz, List<Field> fields) {
		for (Field field : clazz.getDeclaredFields()) {
			if (!fields.contains(field) && matches(field)) {
				fields.add(field);
				field.setAccessible(true);
			}
		}

		for (Field field : clazz.getFields()) {
			if (!fields.contains(field) && matches(field)) {
				fields.add(field);
				field.setAccessible(true);
			}
		}
	}

	private static boolean matches(Field field) {
		return (field.getModifiers() & Opcodes.ACC_STATIC) == 0 && field.isAnnotationPresent(Option.class);
	}

	public static FieldOption<?> create(Mod owner, Field field) throws IllegalAccessException {
		if (field.isAnnotationPresent(TextFile.class))
			return new FileFieldOption(owner, field);
		else if (field.isAnnotationPresent(Slider.class))
			return new SliderFieldOption(owner, field);
		else if (field.isAnnotationPresent(TextField.class))
			return new TextFieldOption(owner, field);

		return new FieldOption<>(owner, field);
	}

	public FieldOption(Mod owner, Field field) throws IllegalAccessException {
		this.owner = owner;
		name = field.getName();
		get = LOOKUP.unreflectGetter(field);
		set = LOOKUP.unreflectSetter(field);
		annotation = field.getAnnotation(Option.class);
		if (annotation == null)
			throw new IllegalArgumentException(field + " is not annotated with @Option");

		if (getType().equals(KeyBinding.class))
			MinecraftUtils.registerKeyBinding((KeyBinding) getValue());

		String translationKey = annotation.translationKey();
		if (translationKey != null && !translationKey.isEmpty())
			translationKey += ".option";
		else {
			if (field.getDeclaringClass().isAnnotationPresent(AbstractTranslationKey.class))
				translationKey = field.getDeclaringClass().getAnnotation(AbstractTranslationKey.class).value()
						+ ".option";
			else if (owner instanceof Mod)
				translationKey = owner.getTranslationKey("option");
		}

		this.translationKey = translationKey;
	}

	@Override
	public String getName() {
		if (translationKey == null)
			return I18n.translate(name);
		else
			return I18n.translate(translationKey + '.' + name);
	}

	@Override
	public Class<T> getType() {
		return (Class<T>) get.type().returnType();
	}

	@Override
	public T getValue() {
		try {
			return (T) get.invoke(owner);
		} catch (Throwable error) {
			throw new AssertionError("retrieving mod option failed", error);
		}
	}

	@Override
	public void setValue(T value) {
		try {
			if (!Objects.equals(value, getValue()) && owner.onOptionChange(name, value)) {
				set.invoke(owner, value);
				owner.postOptionChange(name, value);
			}
		} catch (Throwable error) {
			throw new AssertionError("setting mod option failed", error);
		}
	}

	@Override
	public String getApplyToAllClass() {
		return annotation.applyToAllClass();
	}

	@Override
	public void setFrom(ModOption<T> option) {
		setValue(option.getValue());
	}

	@Override
	public int getPriority() {
		return annotation.priority();
	}

}
