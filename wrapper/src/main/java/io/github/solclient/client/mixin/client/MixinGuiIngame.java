package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.render.hooks.EntityRendererHandler;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.extension.MinecraftExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

	@Final
	@Shadow
	private Minecraft mc;

	@Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer"
			+ "/GlStateManager;enableBlend()V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
	public void preRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		if (Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "renderGameOverlay", at = @At("RETURN"))
	public void postRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		Client.INSTANCE.getEvents().post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL));
	}

	@Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;"
			+ "showCrosshair()Z"))
	public boolean preRenderCrosshair(GuiIngame guiIngame) {
		boolean result = !Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(MinecraftExtension.getInstance().getTimerSC().renderPartialTicks,
						GameOverlayElement.CROSSHAIRS)).cancelled
				&& showCrosshair();
		mc.getTextureManager().bindTexture(Gui.icons);
		return result;
	}

	@Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;"
			+ "drawTexturedModalRect(IIIIII)V"))
	public void overrideCrosshair(float partialTicks, CallbackInfo ci) {
		Client.INSTANCE.getEvents()
				.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.CROSSHAIRS));
	}

	@Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
	public void overrideScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo callback) {
		if (((EntityRendererHandler.IEntityRenderer) Minecraft.getMinecraft().entityRenderer)
				.replayModRender_getHandler() != null
				|| Client.INSTANCE.getEvents().post(new ScoreboardRenderEvent(objective, scaledRes)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "renderHorseJumpBar", at = @At("HEAD"), cancellable = true)
	public void preJumpBar(ScaledResolution scaledRes, int x, CallbackInfo callback) {
		if (Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(MinecraftExtension.getInstance().getTimerSC().renderPartialTicks,
						GameOverlayElement.JUMPBAR)).cancelled) {
			mc.getTextureManager().bindTexture(Gui.icons);
			callback.cancel();
		}
	}

	@Inject(method = "renderHorseJumpBar", at = @At("RETURN"))
	public void postJumpBar(ScaledResolution scaledRes, int x, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new PostGameOverlayRenderEvent(
				MinecraftExtension.getInstance().getTimerSC().renderPartialTicks, GameOverlayElement.JUMPBAR));
	}

	@Shadow
	protected abstract boolean showCrosshair();

}
