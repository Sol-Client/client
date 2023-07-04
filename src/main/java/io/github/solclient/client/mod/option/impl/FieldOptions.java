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

package io.github.solclient.client.mod.option.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.Opcodes;

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import lombok.experimental.UtilityClass;
import net.minecraft.client.option.KeyBinding;

/**
 * Implementation of field-based options.
 */
@UtilityClass
public class FieldOptions {

	public void visit(Mod mod, Consumer<ModOption<?>> visitor) throws IllegalAccessException {
		visit(mod, mod.getClass(), visitor);
	}

	public void visit(Mod mod, Class<?> clazz, Consumer<ModOption<?>> visitor)
			throws IllegalAccessException {
		if (clazz == null)
			return;

		for (Field field : clazz.getDeclaredFields()) {
			if ((field.getModifiers() & Opcodes.ACC_STATIC) != 0 || !field.isAnnotationPresent(Option.class))
				continue;

			field.setAccessible(true);
			visitor.accept(createOption(mod, field));
		}

		// can't safely cast
		Class<?> superclass = clazz.getSuperclass();
		if (superclass == Object.class)
			return;

		visit(mod, (Class) clazz.getSuperclass(), visitor);
	}

	public ModOption<?> createOption(Mod owner, Field field) throws IllegalAccessException {
		Option annotation = field.getAnnotation(Option.class);
		if (annotation == null)
			throw new IllegalArgumentException(field + " is not annotated with @Option");

		String name;
		String key = field.getAnnotation(Option.class).translationKey();
		if (key.isEmpty()) {
			if (field.getDeclaringClass().isAnnotationPresent(AbstractTranslationKey.class))
				name = field.getDeclaringClass().getAnnotation(AbstractTranslationKey.class).value() + ".option."
						+ field.getName();
			else
				name = owner.getTranslationKey("option." + field.getName());
		} else
			name = key + ".option." + field.getName();

		ModOptionStorage<?> storage = new FieldStorage<>(owner, field);

		if (field.getType() == boolean.class)
			return new ToggleOption(name, storage.unsafeCast());
		else if (field.getType() == Colour.class) {
			Optional<String> applyKey = Optional.empty();
			if (field.isAnnotationPresent(ColourKey.class))
				applyKey = Optional.of(field.getAnnotation(ColourKey.class).value())
						.filter((string) -> !string.isEmpty());
			return new ColourOption(name, storage.unsafeCast(), applyKey);
		} else if (field.getType() == String.class) {
			if (field.isAnnotationPresent(TextFile.class))
				return createTextFile(storage, field, name);
			return createTextField(storage, field, name);
		} else if (field.getType() == KeyBinding.class) {
			ModOptionStorage<KeyBinding> keyOption = storage.unsafeCast();
			MinecraftUtils.registerKeyBinding(keyOption.get());
			return new KeyBindingOption(name, keyOption);
		} else if (field.isAnnotationPresent(Slider.class))
			return createSlider(storage, field, name);
		else if (field.getType().isEnum())
			return new EnumOption<>(name, storage.unsafeCast());

		throw new IllegalArgumentException("I don't know how to handle " + field);
	}

	private ModOption<?> createTextField(ModOptionStorage<?> storage, Field field, String name) {
		TextField annotation = field.getAnnotation(TextField.class);
		String placeholder;
		if (annotation != null)
			placeholder = annotation.value();
		else
			placeholder = "";
		return new TextFieldOption(name, storage.unsafeCast(), placeholder);
	}

	private ModOption<?> createTextFile(ModOptionStorage<?> storage, Field field, String name) {
		Mod owner = ((FieldStorage<?>) storage).getOwner();

		TextFile file = field.getAnnotation(TextFile.class);

		if (file.value().indexOf(File.separatorChar) != -1 || file.value().indexOf('/') != -1
				|| /* avoid "it works on my machine" */ file.value().indexOf('\\') != -1)
			throw new IllegalArgumentException("@TextField value must not contain slashes");

		Path path = SolClient.INSTANCE.getConfigFolder().resolve(file.value());
		return new TextFileOption(owner, name, storage.unsafeCast(), path, file.header(), file.text());
	}

	private ModOption<?> createSlider(ModOptionStorage<?> storage, Field field, String name) {
		if (field.getType() != float.class && field.getType() != int.class)
			throw new IllegalArgumentException("Slider " + field + " is not a float or int");

		Slider slider = field.getAnnotation(Slider.class);
		return new SliderOption(name, storage.unsafeCast(), Optional.of(slider.format()), slider.min(), slider.max(),
				slider.step());
	}

}
