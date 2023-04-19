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

package io.github.solclient.client.mod.impl.v1_7visuals.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.v1_7visuals.V1_7VisualsMod;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;

@Mixin(ArmorFeatureRenderer.class)
public class LayerArmorBaseMixin {

	@Inject(method = "combineTextures", at = @At("HEAD"), cancellable = true)
	public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
		if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage)
			callback.setReturnValue(true);
	}

}
