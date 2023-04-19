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

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenHandler;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen {

	public InventoryScreenMixin(ScreenHandler screenHandler) {
		super(screenHandler);
	}

	@Redirect(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/I18n;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
	public String overrideLevel(String key, Object[] parameters) {
		if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("enchantment.level.")) {
			return Integer.toString(Integer.parseInt(key.substring(18)));
		}

		return I18n.translate(key, parameters);
	}

	@Redirect(method = "applyStatusEffectOffset", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;x:I", ordinal = 0))
	public void shiftLeft(InventoryScreen instance, int value) {
		if (TweaksMod.enabled && TweaksMod.instance.centredInventory) {
			x = (width - backgroundWidth) / 2;
			return;
		}

		x = value;
	}

}