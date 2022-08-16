package io.github.solclient.client.wrapper.transformer;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CaptureClassNames {

	Class<?>[] value();

}
