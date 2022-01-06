package me.mcblueparrot.client.mod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.mcblueparrot.client.mod.annotation.ConfigOption;

public class ConfigOptionData {

	public Mod mod;
	public String name;
	public Field field;
	public int priority;
	public boolean common;

	public ConfigOptionData(Mod mod, ConfigOption option, Field field) {
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

	public static List<ConfigOptionData> get(Mod mod) {
		List<ConfigOptionData> result = new ArrayList<ConfigOptionData>();
		add(mod, mod.getClass(), result);
		return result;
	}

	private static List<ConfigOptionData> add(Mod mod, Class<? extends Mod> clazz, List<ConfigOptionData> list) {
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
					list.add(new ConfigOptionData(mod, field.getAnnotation(ConfigOption.class), field));
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