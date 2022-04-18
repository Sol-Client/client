package me.mcblueparrot.client.mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.annotation.AbstractTranslationKey;
import me.mcblueparrot.client.mod.annotation.ApplyToAll;
import me.mcblueparrot.client.mod.annotation.FileOption;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ModOption {

	private final Mod mod;
	@Getter
	private final Field field;
	private String translationKey;
	private final int priority;
	private FileOption configFile;
	@Getter
	private File file;

	public ModOption(Mod mod, Option option, Field field) throws IOException {
		this.mod = mod;
		this.field = field;

		if(field != null) {
			field.setAccessible(true);

			if(field.isAnnotationPresent(FileOption.class)) {
				configFile = field.getAnnotation(FileOption.class);

				file = new File(Minecraft.getMinecraft().mcDataDir, configFile.file());
				readFile();
			}
		}

		priority = option.priority();

		if(!option.translationKey().isEmpty()) {
			translationKey = option.translationKey();
		}

		if(translationKey == null) {
			if(field.getDeclaringClass().isAnnotationPresent(AbstractTranslationKey.class)) {
				translationKey = field.getDeclaringClass().getAnnotation(AbstractTranslationKey.class).value();
			}
			else {
				translationKey = mod.getTranslationKey();
			}
		}
	}

	public boolean isFile() {
		return file != null;
	}

	public String getEditText() {
		return I18n.format(configFile.text());
	}

	public void readFile() throws IOException {
		if(!file.exists()) {
			FileUtils.writeStringToFile(file, configFile.header());
		}

		String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		setValue(contents);
		mod.onFileUpdate(field.getName());
	}

	public String getName() {
		return I18n.format(translationKey + ".option." + field.getName());
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Object getValue() {
		try {
			return field.get(mod);
		}
		catch(IllegalArgumentException | IllegalAccessException error) {
			throw new IllegalStateException(error);
		}
	}

	public void setValue(Object value) {
		try {
			if(!value.equals(field.get(mod))) {
				if(mod.onOptionChange(field.getName(), value)) {
					field.set(mod, value);
					mod.postOptionChange(field.getName(), value);
				}
			}
		}
		catch(IllegalArgumentException | IllegalAccessException error) {
			throw new IllegalStateException(error);
		}
	}

	public static List<ModOption> get(Mod mod) throws IOException {
		List<ModOption> result = new ArrayList<ModOption>();
		add(mod, mod.getClass(), result);

		if(mod instanceof ConfigOnlyMod) {
			result.remove(0);
		}

		return result;
	}

	private static List<ModOption> add(Mod mod, Class<? extends Mod> clazz, List<ModOption> list) throws IOException {
		List<Field> fields = new ArrayList<Field>();

		for(Field field : clazz.getDeclaredFields()) {
			if(!fields.contains(field)) {
				fields.add(field);
			}
		}

		for(Field field : clazz.getFields()) {
			if(!fields.contains(field)) {
				fields.add(field);
			}
		}

		for(Field field : fields) {
			if(field.isAnnotationPresent(Option.class)) {
				if(list.stream().noneMatch((cached) -> cached.field.equals(field))) {
					list.add(new ModOption(mod, field.getAnnotation(Option.class), field));
				}
			}
		}

		if(clazz.getSuperclass() != null && Mod.class.isAssignableFrom(clazz.getSuperclass())) {
			add(mod, (Class<? extends Mod>) clazz.getSuperclass(), list);
		}

		list.sort(Comparator.comparingInt(c -> -c.priority));

		return list;
	}

	public boolean canApplyToAll() {
		return field.isAnnotationPresent(ApplyToAll.class);
	}

	public String getApplyToAllId() {
		return canApplyToAll() ? field.getAnnotation(ApplyToAll.class).value() : null;
	}

	public void applyToAll() {
		String id = getApplyToAllId();

		if(id == null) {
			return;
		}

		for(Mod mod : Client.INSTANCE.getMods()) {
			for(ModOption option : mod.getOptions()) {
				if(option.canApplyToAll() && option.getApplyToAllId().equals(id)
						&& option.getType() == getType()) {
					option.setValue(getValue());
				}
			}
		}
	}

}
