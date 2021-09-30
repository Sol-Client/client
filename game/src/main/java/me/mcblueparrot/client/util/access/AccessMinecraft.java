package me.mcblueparrot.client.util.access;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


// For some reason you do need a surrogate duck.
public interface AccessMinecraft {

    boolean isRunning();

    Timer getTimer();

    static AccessMinecraft getInstance() {
        return (AccessMinecraft) Minecraft.getMinecraft();
    }

}
