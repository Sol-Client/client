package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.RenderGuiBackgroundEvent;
import me.mcblueparrot.client.util.access.AccessGuiScreen;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(GuiScreen.class)
public class MixinGuiScreen implements AccessGuiScreen {

    public boolean canBeForceClosed() {
        return true;
    }

    @Redirect(method = "drawWorldBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"))
    public void getTopColour(GuiScreen guiScreen, int left, int top, int right, int bottom, int startColor,
                           int endColor) {
        if(!Client.INSTANCE.bus.post(new RenderGuiBackgroundEvent()).cancelled) {
            Utils.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }
        else {
            Utils.drawGradientRect(left, top, right, bottom, 0, 0);
        }
    }

}
