package io.github.solclient.client.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.experimental.StandardException;

public class WrappedMain {

	public static void main(String[] args) throws ReflectiveOperationException {
		List<String> argsList = new ArrayList<>(Arrays.asList(args));
		String version = require(argsList, "minecraftVersion");
		args = argsList.toArray(new String[0]);
		try {
			WrapperClassLoader.INSTANCE.loadClass("io.github.solclient.client.v" + version.replace(".", "_") + ".Bootstrap").getMethod("init").invoke(null);
			WrapperClassLoader.INSTANCE.loadClass("net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null,
					(Object) args);
		}
		catch(ClassNotFoundException error) {
			System.err.println("Could not find main class.");
			System.err.println("Please ensure Minecraft and the client impl is on the classpath.");
			throw error;
		}
	}

	private static String require(List<String> args, String label) {
		return get(args, label).orElseThrow(() -> BootstrapArgumentError.label(label));
	}

	private static Optional<String> get(List<String> args, String label) {
		label = "-bootstrap:" + label;
		int index = args.indexOf(label);

		if(index == -1) {
			index = args.indexOf("-" + label);
		}

		if(index == -1 || index >= args.size() - 1) {
			return Optional.empty();
		}
		args.remove(index);
		return Optional.of(args.remove(index));
	}

	@StandardException
	private static class BootstrapArgumentError extends Error {

		public static BootstrapArgumentError label(String label) {
			return new BootstrapArgumentError("Missing --" + label + " in args");
		}

	}

}
