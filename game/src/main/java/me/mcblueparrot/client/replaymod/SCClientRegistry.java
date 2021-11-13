package me.mcblueparrot.client.replaymod;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.annotation.semantic.ForgeCompat;
import net.minecraft.client.settings.KeyBinding;

@Deprecated
@ForgeCompat
public class SCClientRegistry {

    public static void registerKeyBinding(KeyBinding keyBinding) {
        Client.INSTANCE.registerKeyBinding(keyBinding);
    }

}
