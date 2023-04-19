/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.core.mixins.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.render.hooks.EntityRendererHandler;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.scoreboard.ScoreboardObjective;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform"
			+ "/GlStateManager;enableBlend()V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
	public void preRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		if (EventBus.INSTANCE.post(new PreGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void postRenderGameOverlay(float partialTicks, CallbackInfo callback) {
		GlStateManager.disableLighting();
		EventBus.INSTANCE.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.ALL));
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;showCrosshair()Z"))
	public boolean preRenderCrosshair(InGameHud instance) {
		boolean result = !EventBus.INSTANCE.post(
				new PreGameOverlayRenderEvent(MinecraftUtils.getTickDelta(), GameOverlayElement.CROSSHAIRS)).cancelled
				&& showCrosshair();
		client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		return result;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V"))
	public void overrideCrosshair(float partialTicks, CallbackInfo ci) {
		EventBus.INSTANCE.post(new PostGameOverlayRenderEvent(partialTicks, GameOverlayElement.CROSSHAIRS));
	}

	@Inject(method = "renderScoreboardObjective", at = @At("HEAD"), cancellable = true)
	public void overrideScoreboard(ScoreboardObjective objective, Window window, CallbackInfo callback) {
		if (((EntityRendererHandler.IEntityRenderer) MinecraftClient.getInstance().gameRenderer)
				.replayModRender_getHandler() != null
				|| EventBus.INSTANCE.post(new ScoreboardRenderEvent(objective, window)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(method = "renderHorseHealth", at = @At("HEAD"), cancellable = true)
	public void preJumpBar(Window window, int x, CallbackInfo callback) {
		if (EventBus.INSTANCE.post(
				new PreGameOverlayRenderEvent(MinecraftUtils.getTickDelta(), GameOverlayElement.JUMPBAR)).cancelled)
			callback.cancel();

		client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
	}

	@Inject(method = "renderHorseHealth", at = @At("RETURN"))
	public void postJumpBar(Window window, int x, CallbackInfo callback) {
		EventBus.INSTANCE
				.post(new PostGameOverlayRenderEvent(MinecraftUtils.getTickDelta(), GameOverlayElement.JUMPBAR));
	}

	@Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
	public void preBossBar(CallbackInfo callback) {
		if (EventBus.INSTANCE.post(new PreGameOverlayRenderEvent(MinecraftUtils.getTickDelta(),
				GameOverlayElement.BOSSHEALTH)).cancelled) {
			BossBar.framesToLive--;
			callback.cancel();
		}

		client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
	}

	@Inject(method = "renderBossBar", at = @At("RETURN"))
	public void postBossBar(CallbackInfo callback) {
		EventBus.INSTANCE
				.post(new PostGameOverlayRenderEvent(MinecraftUtils.getTickDelta(), GameOverlayElement.BOSSHEALTH));
	}

	@Shadow
	protected abstract boolean showCrosshair();

	@Shadow
	private @Final MinecraftClient client;

}
