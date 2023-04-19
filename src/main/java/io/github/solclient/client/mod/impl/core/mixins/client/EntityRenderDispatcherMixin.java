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

import com.replaymod.replay.camera.CameraEntity;

import io.github.solclient.client.culling.Cullable;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "method_6913", at = @At("HEAD"), cancellable = true)
	public void cullEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks,
			boolean hideDebugBox, CallbackInfoReturnable<Boolean> callback) {
		if (entity instanceof CameraEntity) {
			callback.setReturnValue(textureManager == null);
		}

		if (((Cullable) entity).isCulled()) {
			((EntityRendererAccessor<Entity>) getRenderer(entity)).renderName(entity, x, y, z);
			callback.setReturnValue(textureManager == null);
		}
	}

	@Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
	public void hitboxEvent(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks,
			CallbackInfo callback) {
		if (EventBus.INSTANCE
				.post(new HitboxRenderEvent(entityIn, x, y, z, entityYaw, partialTicks)).cancelled) {
			callback.cancel();
		}
	}

	private static final String UPDATE_CAMERA = "updateCamera(Lnet/minecraft/world/World;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/client/option/GameOptions;F)V";
	private static float sc$yaw, sc$prevYaw, sc$pitch, sc$prevPitch;

	@Inject(method = UPDATE_CAMERA, at = @At("HEAD"))
	public void rotationEvent(World world, TextRenderer textRenderer, Entity entity, Entity entity2, GameOptions gameOptions,
			float tickDelta, CallbackInfo callback) {
		CameraRotateEvent event = EventBus.INSTANCE.post(new CameraRotateEvent(entity.yaw, entity.pitch, 0));
		sc$yaw = event.yaw;
		sc$pitch = event.pitch;

		event = EventBus.INSTANCE.post(new CameraRotateEvent(entity.prevYaw, entity.prevPitch, 0));
		sc$prevYaw = event.yaw;
		sc$prevPitch = event.pitch;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	public float eventYaw(Entity ignored) {
		return sc$yaw;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
	public float eventPrevYaw(Entity ignored) {
		return sc$prevYaw;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	public float eventPitch(Entity ignored) {
		return sc$pitch;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
	public float eventPrevPitch(Entity ignored) {
		return sc$prevPitch;
	}

	@Shadow
	public TextureManager textureManager;

	@Shadow
	public abstract <T extends Entity> EntityRenderer<T> getRenderer(Entity entity);

}
