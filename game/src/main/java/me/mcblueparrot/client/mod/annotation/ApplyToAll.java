package me.mcblueparrot.client.mod.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyToAll {

	String value();

}
