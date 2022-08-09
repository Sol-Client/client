package io.github.solclient.client.v1_19_2.mixins.platform;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.Environment;
import io.github.solclient.client.platform.mc.OptimisationEngine;
import io.github.solclient.client.wrapper.WrapperClassLoader;
import io.github.solclient.client.wrapper.transformer.impl.optifine.OptiFineTransformer;
import net.minecraft.SharedConstants;

@Mixin(Environment.class)
public class EnvironmentImpl {

	@Overwrite(remap = false)
	@SuppressWarnings("unchecked")
	private static <T> T get(String key) {
		switch(key) {
			case "CLASS_LOADER":
				return (T) WrapperClassLoader.INSTANCE;
			case "SODIUM":
				return (T) (Object) false; // TODO
			case "OPTIFINE":
				return (T) (Object) OptiFineTransformer.ACTIVE;
			case "OPTIMISATION_ENGINE":
				return OptiFineTransformer.ACTIVE ? (T) OptimisationEngine.OPTIFINE : (T) OptimisationEngine.VANILLA; // TODO
			case "OFFHAND":
				return (T) (Object) true;
			case "BLOCKING":
				return (T) (Object) false;
			case "LWJGL3":
				return (T) (Object) true;
			case "PROPER_PLUGIN_MESSAGE_IDS":
				return (T) (Object) true;
			case "TARGET_VERSION":
				return (T) SharedConstants.getGameVersion().getReleaseTarget();
			case "VERSION_ID":
				return (T) SharedConstants.getGameVersion().getName();
			case "MAJOR_RELEASE":
				return (T) (Object) versionComponent(0);
			case "MINOR_RELEASE":
				return (T) (Object) versionComponent(1);
			case "PATCH_RELEASE":
				return (T) (Object) versionComponent(2);
			case "PROTOCOL_VERSION":
				return (T) (Object) SharedConstants.getGameVersion().getProtocolVersion();
		}

		throw new UnsupportedOperationException(key + " has no value");
	}

	private static int versionComponent(int i) {
		return Integer.parseInt(SharedConstants.getGameVersion().getReleaseTarget().split("\\.")[i]);
	}

}
