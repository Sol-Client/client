package io.github.solclient.wrapper;

import java.lang.invoke.*;

public final class Springboard {

	private static final String MAIN_CLASS = "net.minecraft.client.main.Main";
	private static final MethodType MAIN_METHOD = MethodType.methodType(void.class, String[].class);

	public static void main(String[] args)
			throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException, Throwable {
		Class<?> mainClass = ClassWrapper.INSTANCE.loadClass(MAIN_CLASS);
		MethodHandle mainMethod = MethodHandles.lookup().findStatic(mainClass, "main", MAIN_METHOD);
		mainMethod.invokeExact(args);
	}

}
