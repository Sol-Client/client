package me.mcblueparrot.client.replaymod;

import me.mcblueparrot.client.annotation.semantic.ForgeCompat;
import net.minecraft.client.renderer.RenderGlobal;

@Deprecated
@ForgeCompat
public class SCForgeHooksClient {

    public static boolean renderFirstPersonHand(RenderGlobal context, float partialTicks, int renderPass) {
        return false; // Always render hand
    }

}
