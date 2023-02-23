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

package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.TNTTimerMod;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.TntEntity;

public class TNTTimerModMixins {

	@Mixin(TntEntityRenderer.class)
	public static abstract class TntEntityRendererMixin extends EntityRenderer<TntEntity> {

		protected TntEntityRendererMixin(EntityRenderDispatcher dispatcher) {
			super(dispatcher);
		}

		// i may have followed the axolotl for this one...
		@Inject(method = "render(Lnet/minecraft/entity/TntEntity;DDDFF)V", at = @At("RETURN"))
		protected void method_10208(TntEntity tnt, double x, double y, double z, float yaw, float tickDelta, CallbackInfo callback) {
			if (TNTTimerMod.enabled)
				renderLabelIfPresent(tnt, TNTTimerMod.getText(tnt), x, y, z, 64);
		}

	}

}
