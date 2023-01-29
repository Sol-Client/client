package io.github.solclient.client.mod.option.annotation;

import java.lang.annotation.*;

/**
 * Use this annotation if you have options on an abstract mod class.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AbstractTranslationKey {

	String value();

}
