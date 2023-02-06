package io.github.solclient.client.mod.option.annotation;

public @interface ColourKey {

	String value() default "";

	String BACKGROUND_COLOUR = "background_colour";
	String BORDER_COLOUR = "border_colour";
	String TEXT_COLOUR = "text_colour";

}
