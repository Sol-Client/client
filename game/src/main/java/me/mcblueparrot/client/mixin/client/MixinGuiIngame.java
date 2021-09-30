package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.CrosshairRenderEvent;
import me.mcblueparrot.client.events.RenderEvent;
import me.mcblueparrot.client.events.ScoreboardRenderEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    public void renderGameOverlay(float partialTicks, CallbackInfo callback) {
        Client.INSTANCE.bus.post(new RenderEvent());
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;" +
            "showCrosshair()Z"))
    public boolean overrideCrosshair(GuiIngame guiIngame) {
        return !Client.INSTANCE.bus.post(new CrosshairRenderEvent()).cancelled;
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void overrideScoreboard(ScoreObjective objective, ScaledResolution scaledRes,
                                              CallbackInfo callback) {
        if(Client.INSTANCE.bus.post(new ScoreboardRenderEvent(objective, scaledRes)).cancelled) {
            callback.cancel();
        }
    }

}
