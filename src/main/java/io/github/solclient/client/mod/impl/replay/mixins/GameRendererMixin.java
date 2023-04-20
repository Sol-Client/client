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

package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.core.events.*;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	public void skipRenderHand(float partialTicks, int xOffset, CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		if (PreRenderHandCallback.EVENT.invoker().preRenderHand())
			callback.cancel();
	}

	@Inject(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;"
			+ "swap(Ljava/lang/String;)V", ordinal = 18, shift = At.Shift.BEFORE))
	public void postRenderWorld(int pass, float partialTicks, long finishTimeNano, CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		PostRenderWorldCallback.EVENT.invoker().postRenderWorld(new MatrixStack());
	}

}
