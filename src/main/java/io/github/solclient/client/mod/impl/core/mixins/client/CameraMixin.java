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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.CameraRotateEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Camera.class)
public class CameraMixin {

	private static float sc$yaw, sc$pitch;

	@Inject(method = "update", at = @At("HEAD"))
	private static void rotationEvent(PlayerEntity player, boolean thirdPerson, CallbackInfo callback) {
		CameraRotateEvent event = EventBus.INSTANCE.post(new CameraRotateEvent(player.yaw, player.pitch, 0));
		sc$yaw = event.yaw;
		sc$pitch = event.pitch;
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw:F"))
	private static float eventYaw(PlayerEntity ignored) {
		return sc$yaw;
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch:F"))
	private static float eventPitch(PlayerEntity ignored) {
		return sc$pitch;
	}

}
