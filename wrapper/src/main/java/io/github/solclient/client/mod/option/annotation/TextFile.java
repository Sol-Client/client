package io.github.solclient.client.mod.option.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TextFile {

	String value();

	String text() default "sol_client.file.edit";

	String header();

}
