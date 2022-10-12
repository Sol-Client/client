package io.github.solclient.client.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Marks an event handler method for when a class is registered.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {

}