package me.mcblueparrot.client.mod.impl.replay.fix;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.annotation.ForgeCompat;
import net.minecraft.client.settings.KeyBinding;

@Deprecated
@ForgeCompat
public class SCClientRegistry {

	public static void registerKeyBinding(KeyBinding keyBinding) {
		Client.INSTANCE.registerKeyBinding(keyBinding);
	}

}
