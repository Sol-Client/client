package io.github.solclient.client.v1_19;

import org.spongepowered.asm.mixin.Mixins;

public class Bootstrap {

	public static void init() {
		Mixins.addConfiguration("sol-client-mixins-1.19.json");
	}

}
