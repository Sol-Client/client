package io.github.solclient.client.mod.annotation;

import java.lang.annotation.*;

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
