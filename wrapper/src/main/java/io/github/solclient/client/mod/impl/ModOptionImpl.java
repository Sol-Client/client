package io.github.solclient.client.mod.impl;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

// this is very old...
// don't touch this
// instead, request for more methods to be added to the interface
@Deprecated
public final class ModOptionImpl<T> implements ModOption<T> {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Mod mod;
	@Getter
	private final Field field;
	private String translationKey;
	@Getter
	private final int priority;
	private FileOption configFile;
	@Getter
	private File file;
	private String applyToAllClass;
	@Getter
	private String placeholder;

	public ModOptionImpl(Mod mod, Option option, Field field) throws IOException {
		this.mod = mod;
		this.field = field;

		field.setAccessible(true);

		if (field.isAnnotationPresent(FileOption.class)) {
			configFile = field.getAnnotation(FileOption.class);

			file = new File(MinecraftClient.getInstance().runDirectory, configFile.file());
			readFile();
		}

		if (field.isAnnotationPresent(StringOption.class)) {
			placeholder = field.getAnnotation(StringOption.class).value();
		}

		String name = field.getDeclaringClass() + "." + field.getName();

		if (Modifier.isFinal(field.getModifiers()) && !(field.getType() == KeyBinding.class))
			LOGGER.warn("Mod option {} is final", name);

		if (getValue() == null)
			LOGGER.warn("Mod option {} has no default value. This may cause a crash.", name);

		if (field.getType() == KeyBinding.class)
			MinecraftUtils.registerKeyBinding((KeyBinding) getValue());

		priority = option.priority();

		if (!option.translationKey().isEmpty())
			translationKey = option.translationKey() + ".option";

		if (translationKey == null) {
			if (field.getDeclaringClass().isAnnotationPresent(AbstractTranslationKey.class))
				translationKey = field.getDeclaringClass().getAnnotation(AbstractTranslationKey.class).value() + ".option";
			else
				translationKey = mod.getTranslationKey("option");
		}

		applyToAllClass = option.applyToAllClass();

		if (applyToAllClass.isEmpty())
			applyToAllClass = null;
	}

	// nooooo!!
	// move this to FileOption
	public boolean isFile() {
		return file != null;
	}

	public String getEditText() {
		return I18n.translate(configFile.text());
	}

	public void readFile() throws IOException {
		if (!file.exists()) {
			FileUtils.writeStringToFile(file, configFile.header());
		}

		String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		setValue((T) contents);
		mod.onFileUpdate(field.getName());
	}

	public String getName() {
		return I18n.translate(translationKey + "." + field.getName());
	}

	public Class<T> getType() {
		return (Class<T>) field.getType();
	}

	public T getValue() {
		try {
			return (T) field.get(mod);
		} catch (IllegalArgumentException | IllegalAccessException error) {
			throw new AssertionError(error);
		}
	}

	public void setValue(T value) {
		try {
			if (!value.equals(field.get(mod)) && mod.onOptionChange(field.getName(), value)) {
				field.set(mod, value);
				mod.postOptionChange(field.getName(), value);
			}
		} catch (IllegalArgumentException | IllegalAccessException error) {
			throw new IllegalStateException(error);
		}
	}

	public static List<ModOption<?>> get(Mod mod) throws IOException {
		List<ModOption<?>> result = new ArrayList<>();
		add(mod, mod.getClass(), result);

		if (mod instanceof ConfigOnlyMod)
			result.remove(0);

		return result;
	}

	private static List<ModOption<?>> add(Mod mod, Class<? extends Mod> clazz, List<ModOption<?>> list)
			throws IOException {
		List<Field> fields = new ArrayList<Field>();

		for (Field field : clazz.getDeclaredFields())
			if (!fields.contains(field))
				fields.add(field);

		for (Field field : clazz.getFields())
			if (!fields.contains(field))
				fields.add(field);

		for (Field field : fields)
			if (field.isAnnotationPresent(Option.class)
					&& list.stream().noneMatch((item) -> ((ModOptionImpl) item).field.equals(field)))
				list.add(new ModOptionImpl<>(mod, field.getAnnotation(Option.class), field));

		if (clazz.getSuperclass() != null && Mod.class.isAssignableFrom(clazz.getSuperclass()))
			add(mod, (Class<? extends Mod>) clazz.getSuperclass(), list);

		list.sort(Comparator.comparingInt((item) -> -item.getPriority()));

		return list;
	}

	@Override
	public boolean canApplyToAll() {
		return applyToAllClass != null;
	}

	@Override
	public void applyToAll() {
		String key = applyToAllClass;

		if (key == null)
			return;

		for (Mod mod : Client.INSTANCE.getMods()) {
			for (ModOption<?> option : mod.getOptions()) {
				if (option.getType() == getType() && ((ModOption<T>) option).isEquivalent(this))
					((ModOption<T>) option).setFrom(this);
			}
		}
	}

	@Override
	public boolean isEquivalent(ModOption<T> option) {
		return option instanceof ModOptionImpl && ((ModOptionImpl) option).applyToAllClass.equals(applyToAllClass);
	}

	@Override
	public void setFrom(ModOption<T> option) {
		setValue(option.getValue());
	}

}
