package io.github.solclient.client.mod.option.impl;

import java.lang.reflect.Field;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.TextOption;
import io.github.solclient.client.mod.option.annotation.*;

public class TextFieldOption extends FieldOption<String> implements TextOption {

	private final TextField annotation;

	public TextFieldOption(Mod owner, Field field) throws IllegalAccessException {
		super(owner, field);
		annotation = field.getAnnotation(TextField.class);
		if (annotation == null)
			throw new IllegalArgumentException(field + " is not annotated with @TextField");
	}

	@Override
	public String getPlaceholder() {
		return annotation.value();
	}

}
