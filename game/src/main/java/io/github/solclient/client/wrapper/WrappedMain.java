package io.github.solclient.client.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.github.solclient.client.Constants;
import lombok.experimental.StandardException;

public class WrappedMain {

	public static void main(String[] args) throws ReflectiveOperationException {
		if(Constants.MC_VERSION == null) {
			System.err.println("No Minecraft version specified - please do so with -Dio.github.solclient.client.mc_version=x.x.x");
			System.exit(1);
		}

		try {
			WrapperClassLoader.INSTANCE.loadClass("io.github.solclient.client.v" + Constants.MC_VERSION.replace(".", "_") + ".Bootstrap").getMethod("init", WrapperClassLoader.class).invoke(null, WrapperClassLoader.INSTANCE);
			WrapperClassLoader.INSTANCE.loadClass("net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null,
					(Object) args);
		}
		catch(ClassNotFoundException error) {
			System.err.println("Could not find main class.");
			System.err.println("Please ensure Minecraft and the client impl is on the classpath.");
			throw error;
		}
	}

}
