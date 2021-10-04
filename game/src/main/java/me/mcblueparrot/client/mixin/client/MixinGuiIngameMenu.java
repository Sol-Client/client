
package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.mod.ModsScreen;
import me.mcblueparrot.client.ui.IngameServerList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void addButtons(CallbackInfo callback) {
        buttonList.add(new GuiButton(8, width / 2 - 100, height / 4 + 56, 98, 20,
                "Mods"));
        buttonList.add(new GuiButton(9, width / 2 + 2, height / 4 + 56, 98, 20,
                "Servers"));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, CallbackInfo callback) {
        if(button.id == 8) {
            mc.displayGuiScreen(new ModsScreen(this));
        }
        else if(button.id == 9) {
            mc.displayGuiScreen(new IngameServerList(this));
        }
    }

}
