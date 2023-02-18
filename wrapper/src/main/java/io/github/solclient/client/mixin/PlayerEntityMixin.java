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

package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepStatus;
import net.minecraft.util.math.BlockPos;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	public void attackEntity(Entity entity, CallbackInfo callback) {
		if (entity.isAttackable()) {
			Client.INSTANCE.getEvents().post(new EntityAttackEvent(entity));
		}
	}

	@Inject(method = "attemptSleep", at = @At("HEAD"))
	public void onSleep(BlockPos pos, CallbackInfoReturnable<SleepStatus> callback) {
		Client.INSTANCE.getEvents().post(new PlayerSleepEvent((PlayerEntity) (Object) this, pos));
	}

}
