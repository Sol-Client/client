package io.github.solclient.client.mod.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Slider {

	float min();

	float max();

	float step();

	boolean showValue() default true;

	String format() default "sol_client.passthrough";

}
