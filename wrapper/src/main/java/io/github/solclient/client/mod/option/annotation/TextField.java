package io.github.solclient.client.mod.option.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

@Retention(RUNTIME)
@Target(FIELD)
public @interface TextField {

	/**
	 * @return the placeholder.
	 */
	String value();

}
