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
import org.spongepowered.asm.mixin.injection.callback.*;

import com.mojang.blaze3d.platform.*;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;gamma:F"))
	public float overrideGamma(GameOptions options) {
		return EventBus.INSTANCE.post(new GammaEvent(options.gamma)).gamma;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;bind(Z)V", shift = At.Shift.BEFORE))
	public void addShaders(float partialTicks, long nanoTime, CallbackInfo callback) {
		for (ShaderEffect effect : EventBus.INSTANCE
				.post(new PostProcessingEvent(PostProcessingEvent.Type.RENDER)).effects) {
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			effect.render(MinecraftUtils.getTickDelta());
			GlStateManager.popMatrix();
		}
	}

	@Inject(method = "onResized", at = @At("RETURN"))
	public void updateShaders(int width, int height, CallbackInfo callback) {
		if (GlProgramManager.getInstance() == null || !GLX.shadersSupported)
			return;

		for (ShaderEffect effect : EventBus.INSTANCE
				.post(new PostProcessingEvent(PostProcessingEvent.Type.UPDATE)).effects) {
			effect.setupDimensions(width, height);
		}
	}

	private static float sc$yaw, sc$prevYaw, sc$pitch, sc$prevPitch;

	@Inject(method = "transformCamera", at = @At("HEAD"))
	public void rotationEvent(float partialTicks, CallbackInfo callback) {
		CameraRotateEvent event = EventBus.INSTANCE
				.post(new CameraRotateEvent(client.getCameraEntity().yaw, client.getCameraEntity().pitch, 0));
		sc$yaw = event.yaw;
		sc$pitch = event.pitch;
		GlStateManager.rotate(event.roll, 0, 0, 1);

		event = EventBus.INSTANCE
				.post(new CameraRotateEvent(client.getCameraEntity().prevYaw, client.getCameraEntity().prevPitch, 0));
		sc$prevYaw = event.yaw;
		sc$prevPitch = event.pitch;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	public float eventYaw(Entity entity) {
		return sc$yaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
	public float eventPrevYaw(Entity entity) {
		return sc$prevYaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	public float eventPitch(Entity entity) {
		return sc$pitch;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
	public float eventPrevPitch(Entity entity) {
		return sc$prevPitch;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;increaseTransforms(FF)V"))
	public void lookinAround(ClientPlayerEntity instance, float yaw, float pitch) {
		PlayerHeadRotateEvent event = new PlayerHeadRotateEvent(yaw, pitch);
		EventBus.INSTANCE.post(event);
		yaw = event.yaw;
		pitch = event.pitch;

		if (!event.cancelled && !MinecraftUtils.isSpectatingEntityInReplay()) {
			instance.increaseTransforms(yaw, pitch);
		}
	}

	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void getFov(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> callback) {
		callback.setReturnValue(
				EventBus.INSTANCE.post(new FovEvent(callback.getReturnValue(), partialTicks)).fov);
	}

	@Redirect(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;shouldRenderBlockOutline()Z"))
	public boolean overrideCanDraw(GameRenderer instance) {
		return true;
	}

	@Redirect(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/block/material/Material;)Z", ordinal = 0))
	public boolean overrideWetBlockHighlight(Entity entity, Material materialIn) {
		boolean maybeWould = entity.isSubmergedIn(materialIn);
		boolean would = maybeWould && shouldRenderBlockOutline();
		if (maybeWould && EventBus.INSTANCE
				.post(new BlockHighlightRenderEvent(client.result, MinecraftUtils.getTickDelta())).cancelled)
			return false;
		return would;
	}

	@Redirect(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/block/material/Material;)Z", ordinal = 1))
	public boolean overrideBlockHighlight(Entity entity, Material materialIn) {
		boolean totallyWouldNot = entity.isSubmergedIn(materialIn);
		boolean wouldNot = totallyWouldNot || !shouldRenderBlockOutline();
		if (!totallyWouldNot && EventBus.INSTANCE
				.post(new BlockHighlightRenderEvent(client.result, MinecraftUtils.getTickDelta())).cancelled)
			return true;
		return wouldNot;
	}

	@Shadow
	private MinecraftClient client;

	@Shadow
	protected abstract boolean shouldRenderBlockOutline();

}
