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

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "closeScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseInput;lockMouse()V"))
	public void afterLock(CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.betterKeyBindings) {
			for (KeyBinding keyBinding : options.allKeys) {
				try {
					KeyBinding.setKeyPressed(keyBinding.getCode(),
							keyBinding.getCode() < 256 && Keyboard.isKeyDown(keyBinding.getCode())); // TODO
																										// modifier
																										// support
				} catch (IndexOutOfBoundsException ignored) {
				}
			}
		}
	}

	@Shadow
	public GameOptions options;

}
