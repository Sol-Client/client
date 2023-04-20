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

package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.*;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
	public void cancelBlockBreakingParticles(BlockPos pos, Direction direction, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.disableBlockParticles)
			callback.cancel();
	}

	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	public void cancelBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.disableBlockParticles)
			callback.cancel();
	}

}
