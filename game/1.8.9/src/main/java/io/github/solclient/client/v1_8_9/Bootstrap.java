package io.github.solclient.client.v1_8_9;

import org.spongepowered.asm.mixin.Mixins;

import io.github.solclient.client.v1_8_9.transformers.ProxyScreenTransformer;
import io.github.solclient.client.wrapper.WrapperClassLoader;

public class Bootstrap {

	public static void init(WrapperClassLoader loader) {
		Mixins.addConfiguration("sol-client-mixins-1.8.9.json");
		loader.registerTransformer(new ProxyScreenTransformer());
	}

}
