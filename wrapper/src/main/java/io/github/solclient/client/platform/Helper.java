package io.github.solclient.client.platform;

import java.lang.annotation.*;

/**
 * Marks "helper" methods, methods that are not strictly in vanilla, but create cleaner code.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Helper {
}
