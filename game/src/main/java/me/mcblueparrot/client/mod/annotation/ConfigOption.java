package me.mcblueparrot.client.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.mcblueparrot.client.mod.Mod;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

	/**
	 * The option's name.
	 * @return The name.
	 */
	String value();

	int priority() default 0;

}
