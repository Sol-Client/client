package io.github.solclient.client.mod.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

@Retention(RUNTIME)
@Target(FIELD)
public @interface StringOption {

	/**
	 * @return the placeholder.
	 */
	String value();

}
