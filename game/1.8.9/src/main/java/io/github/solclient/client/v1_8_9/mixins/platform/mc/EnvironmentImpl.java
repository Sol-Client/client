package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.Environment;
import io.github.solclient.client.platform.mc.OptimisationEngine;
import io.github.solclient.client.wrapper.WrapperClassLoader;
import io.github.solclient.client.wrapper.transformer.impl.optifine.OptiFineTransformer;

@Mixin(Environment.class)
public class EnvironmentImpl {

	@Overwrite(remap = false)
	@SuppressWarnings("unchecked")
	private static <T> T get(String key) {
		switch(key) {
			case "CLASS_LOADER":
				return (T) WrapperClassLoader.INSTANCE;
			case "SODIUM":
				return (T) (Object) false; // sodium is not on 1.8
			case "OPTIFINE":
				return (T) (Object) OptiFineTransformer.ACTIVE;
			case "OPTIMISATION_ENGINE":
				return OptiFineTransformer.ACTIVE ? (T) OptimisationEngine.OPTIFINE : (T) OptimisationEngine.VANILLA;
			case "OFFHAND":
				return (T) (Object) false;
			case "BLOCKING":
				return (T) (Object) true;
			case "LWJGL3":
				return (T) (Object) false;
			case "PROPER_PLUGIN_MESSAGE_IDS":
				return (T) (Object) false;
			case "TARGET_VERSION":
			case "VERSION_ID":
				return (T) "1.8.9";
			case "MAJOR_RELEASE":
				return (T) (Object) 1;
			case "MINOR_RELEASE":
				return (T) (Object) 8;
			case "PATCH_RELEASE":
				return (T) (Object) 9;
			case "PROTOCOL_VERSION":
				return (T) (Object) 47;
			case "MOJANG":
				return (T) "Mojang AB";
		}

		throw new UnsupportedOperationException(key + " has no value");
	}

}
