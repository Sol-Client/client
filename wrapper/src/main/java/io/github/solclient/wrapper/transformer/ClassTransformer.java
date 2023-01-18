package io.github.solclient.wrapper.transformer;

import io.github.solclient.util.GlobalConstants;
import lombok.experimental.UtilityClass;

/**
 * General small class transformations.
 */
@UtilityClass
public class ClassTransformer {

	public byte[] transformClass(String name, byte[] input) {
		input = GuavaTransformer.transform(name, input);
		if (GlobalConstants.DEV && (name.startsWith("net.minecraft.") || name.startsWith("com.mojang.blaze3d.")))
			input = PackageAccessFixer.fix(input);
		input = AccessWidenerTransformer.transform(name, input);
		input = ReplayModMixinPluginCompat.transform(name, input);
		return input;
	}

}
