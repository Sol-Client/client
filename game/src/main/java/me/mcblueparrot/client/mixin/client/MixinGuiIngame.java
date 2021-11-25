package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.render.hooks.EntityRendererHandler;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.GameOverlayElement;
import me.mcblueparrot.client.events.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.events.PreGameOverlayRenderEvent;
import me.mcblueparrot.client.events.ScoreboardRenderEvent;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer" +
            "/GlStateManager;enableBlend()V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    public void preRenderGameOverlay(float partialTicks, CallbackInfo callback) {
        GlStateManager.disableLighting();
        if(Client.INSTANCE.bus.post(new PreGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL)).cancelled) {
            callback.cancel();
        }
    }

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    public void postRenderGameOverlay(float partialTicks, CallbackInfo callback) {
        GlStateManager.disableLighting();
        Client.INSTANCE.bus.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL));
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;" +
            "showCrosshair()Z"))
    public boolean preRenderCrosshair(GuiIngame guiIngame) {
        boolean result =
                showCrosshair() && !Client.INSTANCE.bus.post(new PreGameOverlayRenderEvent(AccessMinecraft.getInstance()
                .getTimerSC().renderPartialTicks, GameOverlayElement.CROSSHAIRS)).cancelled;
        mc.getTextureManager().bindTexture(Gui.icons);
        return result;
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;" +
            "drawTexturedModalRect(IIIIII)V"))
    public void overrideCrosshair(float partialTicks, CallbackInfo ci) {
        Client.INSTANCE.bus.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.CROSSHAIRS));
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void overrideScoreboard(ScoreObjective objective, ScaledResolution scaledRes,
                                              CallbackInfo callback) {
        if(((EntityRendererHandler.IEntityRenderer) Minecraft.getMinecraft().entityRenderer)
                .replayModRender_getHandler() != null || Client.INSTANCE.bus.post(new ScoreboardRenderEvent(objective, scaledRes)).cancelled) {
            callback.cancel();
        }
    }

    @Inject(method = "renderHorseJumpBar", at = @At("HEAD"), cancellable = true)
    public void preJumpBar(ScaledResolution scaledRes, int x, CallbackInfo callback) {
        if(Client.INSTANCE.bus.post(new PreGameOverlayRenderEvent(AccessMinecraft.getInstance().getTimerSC()
                .renderPartialTicks, GameOverlayElement.JUMPBAR)).cancelled) {
            mc.getTextureManager().bindTexture(Gui.icons);
            callback.cancel();
        }
    }

    @Inject(method = "renderHorseJumpBar", at = @At("RETURN"))
    public void postJumpBar(ScaledResolution scaledRes, int x, CallbackInfo callback) {
        Client.INSTANCE.bus.post(new PostGameOverlayRenderEvent(AccessMinecraft.getInstance().getTimerSC()
                .renderPartialTicks, GameOverlayElement.JUMPBAR));
    }

    @Shadow
    protected abstract boolean showCrosshair();

}
