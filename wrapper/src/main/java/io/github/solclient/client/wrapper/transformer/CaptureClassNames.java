package io.github.solclient.client.wrapper.transformer;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface CaptureClassNames {

	Class<?>[] value();

}
