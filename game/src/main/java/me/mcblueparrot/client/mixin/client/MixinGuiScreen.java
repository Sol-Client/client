package me.mcblueparrot.client.mixin.client;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.ActionPerformedEvent;
import me.mcblueparrot.client.events.PostGuiInitEvent;
import me.mcblueparrot.client.events.PostGuiRenderEvent;
import me.mcblueparrot.client.events.PreGuiInitEvent;
import me.mcblueparrot.client.events.PreGuiKeyboardInputEvent;
import me.mcblueparrot.client.events.PreGuiMouseInputEvent;
import me.mcblueparrot.client.events.RenderGuiBackgroundEvent;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@Mixin(GuiScreen.class)
public class MixinGuiScreen implements AccessGuiScreen {

    @Shadow protected Minecraft mc;

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

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;mousePressed(Lnet/minecraft/client/Minecraft;II)Z"))
    public boolean onActionPerformed(GuiButton instance, Minecraft mc, int mouseX, int mouseY) {
        return instance.mousePressed(mc,
                mouseX,
                mouseY) && !Client.INSTANCE.bus.post(new ActionPerformedEvent((GuiScreen) (Object) this, instance)).cancelled;
    }

    @Redirect(method = "setWorldAndResolution", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui" +
            "/GuiScreen;initGui()V"))
    public void guiInit(GuiScreen instance) {
        if(!Client.INSTANCE.bus.post(new PreGuiInitEvent(instance)).cancelled) {
            instance.initGui();
            Client.INSTANCE.bus.post(new PostGuiInitEvent(instance, buttonList));
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void postGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        GlStateManager.color(1, 1, 1, 1); // Prevent colour from leaking
        Client.INSTANCE.bus.post(new PostGuiRenderEvent(partialTicks));

        if(SolClientMod.instance.logoInInventory && (Object) this instanceof GuiContainer) {
            GlStateManager.enableBlend();

            mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
                            Utils.getTextureScale() + ".png"));

            Gui.drawModalRectWithCustomSizedTexture(width - 140, height - 40, 0, 0, 128, 32, 128, 32);
        }
    }

    @Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;" +
            "handleMouseInput()V"))
    public void handleMouseInput(GuiScreen instance) throws IOException {
        if(!Client.INSTANCE.bus.post(new PreGuiMouseInputEvent()).cancelled) {
            instance.handleMouseInput();
        }
    }

    @Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;" +
            "handleKeyboardInput()V"))
    public void handleKeyboardInput(GuiScreen instance) throws IOException {
        if(!Client.INSTANCE.bus.post(new PreGuiKeyboardInputEvent()).cancelled) {
            instance.handleKeyboardInput();
        }
    }
    
    @Shadow
    protected List<GuiButton> buttonList;

    @Shadow
    public int width;

    @Shadow
    public int height;

}
