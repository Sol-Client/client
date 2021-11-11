package me.mcblueparrot.client.util.access;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;


// For some reason you do need a surrogate duck.
public interface AccessMinecraft {

    boolean isRunning();

    Timer getTimerSC();

    static AccessMinecraft getInstance() {
        return (AccessMinecraft) Minecraft.getMinecraft();
    }

}
