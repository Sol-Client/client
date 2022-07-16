package io.github.solclient.client.wrapper;

import java.lang.reflect.InvocationTargetException;

public class BootstrapMain {

	private static final String WRAPPED_MAIN = BootstrapMain.class.getPackage().getName() + ".WrappedMain";

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, InstantiationException {
		WrapperClassLoader.INSTANCE.loadClass(WRAPPED_MAIN).getMethod("main", String[].class).invoke(null,
				(Object) args);
	}

}
