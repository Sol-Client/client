package io.github.solclient.client.mod.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation if you have options on an abstract mod class.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AbstractTranslationKey {

	String value();

}
