package io.github.solclient.client.mod.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileOption {

	String text() default "sol_client.file.edit";

	String file();

	String header();

}
