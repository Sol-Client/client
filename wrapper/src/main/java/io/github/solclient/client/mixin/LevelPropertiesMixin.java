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

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.TimeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelProperties;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin {

	private final MinecraftClient mc = MinecraftClient.getInstance();

	@Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
	public void overrideWorldTime(CallbackInfoReturnable<Long> callback) {
		if (mc.world != null && (Object) this == mc.world.getLevelProperties()) {
			callback.setReturnValue(Client.INSTANCE.getEvents().post(new TimeEvent(timeOfDay)).time);
		}
	}

	@Shadow
	private long timeOfDay;

}
