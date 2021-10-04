package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.ui.BetterLanguageGui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GuiLanguage.class)
public class MixinGuiLanguage extends GuiScreen {

    private BetterLanguageGui betterList;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void overrideList(CallbackInfo callback) {
        betterList = new BetterLanguageGui(mc, (GuiLanguage) (Object) this);
        betterList.registerScrollButtons(7, 7);
    }

    @Inject(method = "handleMouseInput", at = @At("HEAD"), cancellable = true)
    public void overrideMouseInput(CallbackInfo callback) throws IOException {
        callback.cancel();
        super.handleMouseInput();
        betterList.handleMouseInput();
    }


    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    public void overrideRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) throws IOException {
        callback.cancel();
        betterList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(this.fontRendererObj, I18n.format("options.language"), width / 2, 16, -1);
        drawCenteredString(this.fontRendererObj, "(" + I18n.format("options.languageWarning") + ")", width / 2,
                height - 56, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
