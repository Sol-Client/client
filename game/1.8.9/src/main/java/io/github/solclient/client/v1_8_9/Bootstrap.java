package io.github.solclient.client.v1_8_9;

import org.spongepowered.asm.mixin.Mixins;

public class Bootstrap {

	public static void init() {
		Mixins.addConfiguration("sol-client.mixins.json");
	}

}
