package io.github.solclient.client.mod.impl.replay.fix;

import io.github.solclient.client.Client;
import io.github.solclient.client.util.*;
import net.minecraft.client.settings.KeyBinding;

@Deprecated
@ForgeCompat
public class SCClientRegistry {

	public static void registerKeyBinding(KeyBinding keyBinding) {
		Utils.registerKeyBinding(keyBinding);
	}

}
