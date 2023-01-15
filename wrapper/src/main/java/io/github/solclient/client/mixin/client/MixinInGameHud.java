package io.github.solclient.client.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.render.hooks.EntityRendererHandler;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.extension.MinecraftClientExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform"
			+ "/GlStateManager;enableBlend()V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
	public void preRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		if (Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void postRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		Client.INSTANCE.getEvents().post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL));
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;showCrosshair()Z"))
	public boolean preRenderCrosshair(InGameHud instance) {
		boolean result = !Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(MinecraftClientExtension.getInstance().getTicker().tickDelta,
						GameOverlayElement.CROSSHAIRS)).cancelled
				&& showCrosshair();
		client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		return result;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V"))
	public void overrideCrosshair(float partialTicks, CallbackInfo ci) {
		Client.INSTANCE.getEvents()
				.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.CROSSHAIRS));
	}

	@Inject(method = "renderScoreboardObjective", at = @At("HEAD"), cancellable = true)
	public void overrideScoreboard(ScoreboardObjective objective, Window window, CallbackInfo callback) {
		if (((EntityRendererHandler.IEntityRenderer) MinecraftClient.getInstance().gameRenderer)
				.replayModRender_getHandler() != null
				|| Client.INSTANCE.getEvents().post(new ScoreboardRenderEvent(objective, window)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "renderHorseHealth", at = @At("HEAD"), cancellable = true)
	public void preJumpBar(Window window, int x, CallbackInfo callback) {
		if (Client.INSTANCE.getEvents()
				.post(new PreGameOverlayRenderEvent(MinecraftClientExtension.getInstance().getTicker().tickDelta,
						GameOverlayElement.JUMPBAR)).cancelled) {
			client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
			callback.cancel();
		}
	}

	@Inject(method = "renderHorseHealth", at = @At("RETURN"))
	public void postJumpBar(Window window, int x, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new PostGameOverlayRenderEvent(
				MinecraftClientExtension.getInstance().getTicker().tickDelta, GameOverlayElement.JUMPBAR));
	}

	@Shadow
	protected abstract boolean showCrosshair();

	@Shadow
	private @Final MinecraftClient client;

}
