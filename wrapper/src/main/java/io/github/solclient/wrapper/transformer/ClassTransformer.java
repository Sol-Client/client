package io.github.solclient.wrapper.transformer;

import lombok.experimental.UtilityClass;

/**
 * General small class transformations.
 */
@UtilityClass
public class ClassTransformer {

	public byte[] transformClass(String name, byte[] input) {
		input = PackageAccessFixer.fix(name, input);
		input = AccessWidenerTransformer.transform(name, input);
		input = ReplayModMixinPluginCompat.transform(name, input);
		return input;
	}

}
