package me.mcblueparrot.client.util.access;

import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

public interface AccessGuiChat {

    void type(char typedChar, int keyCode);

}
