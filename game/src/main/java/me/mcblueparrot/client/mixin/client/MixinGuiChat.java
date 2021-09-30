package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.util.access.AccessGuiChat;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat implements AccessGuiChat {

    @Override
    @Invoker("keyTyped")
    public abstract void type(char typedChar, int keyCode);

}
