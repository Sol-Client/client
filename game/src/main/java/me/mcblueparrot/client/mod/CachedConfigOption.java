package me.mcblueparrot.client.mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

import me.mcblueparrot.client.mod.annotation.ConfigFile;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.Minecraft;

public class CachedConfigOption {

	public Mod mod;
	public String name;
	public Field field;
	public int priority;
	public boolean common;
	public ConfigFile configFile;
	public File file;

	public CachedConfigOption(Mod mod, ConfigOption option, Field field) throws IOException {
		this.mod = mod;

		if(option != null) {
			name = option.value();
		}

		if(field != null) {
			this.field = field;
			field.setAccessible(true);
		}

		if(option != null) {
			priority = option.priority();
		}

		switch(name) {
			case "Background Colour":
			case "Border Colour":
			case "Text Colour":
				common = true;
				break;
		}

		if(field.isAnnotationPresent(ConfigFile.class)) {
			configFile = field.getAnnotation(ConfigFile.class);

			file = new File(Minecraft.getMinecraft().mcDataDir, configFile.file());
			readFile();
		}
	}

	public void readFile() throws IOException {
		if(!file.exists()) {
			FileUtils.writeStringToFile(file, configFile.header());
		}

		String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		setValue(contents);
		mod.onFileUpdate(field.getName());
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

	public static List<CachedConfigOption> get(Mod mod) throws IOException {
		List<CachedConfigOption> result = new ArrayList<CachedConfigOption>();
		add(mod, mod.getClass(), result);
		return result;
	}

	private static List<CachedConfigOption> add(Mod mod, Class<? extends Mod> clazz, List<CachedConfigOption> list) throws IOException {
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
			if(field.isAnnotationPresent(ConfigOption.class)) {
				if(list.stream().noneMatch((cached) -> cached.field.equals(field))) {
					list.add(new CachedConfigOption(mod, field.getAnnotation(ConfigOption.class), field));
				}
			}
		}

		if(clazz.getSuperclass() != null && Mod.class.isAssignableFrom(clazz.getSuperclass())) {
			add(mod, (Class<? extends Mod>) clazz.getSuperclass(), list);
		}

		list.sort(Comparator.comparingInt(c -> -c.priority));

		return list;
	}

}
