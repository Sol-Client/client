package io.github.solclient.client.v1_19;

import org.spongepowered.asm.mixin.Mixins;

import io.github.solclient.client.wrapper.WrapperClassLoader;

public class Bootstrap {

	public static void init(WrapperClassLoader loader) {
		Mixins.addConfiguration("sol-client-mixins-1.19.json");
	}

}
