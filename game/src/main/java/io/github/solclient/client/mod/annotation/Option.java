package io.github.solclient.client.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.solclient.client.mod.Mod;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

	int priority() default 0;

	String translationKey() default "";

	String applyToAllClass() default "";

	String BACKGROUND_COLOUR_CLASS = "background_colour";
	String BORDER_COLOUR_CLASS = "border_colour";
	String TEXT_COLOUR_CLASS = "text_colour";

}
