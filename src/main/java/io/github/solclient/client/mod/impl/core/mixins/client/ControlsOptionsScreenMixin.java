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

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.util.KeyBindingInterface;
import io.github.solclient.client.util.data.Modifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.*;

@Mixin(ControlsOptionsScreen.class)
public class ControlsOptionsScreenMixin extends Screen {

	@Override
	public void handleKeyboard() {
		if (!Keyboard.getEventKeyState())
			keyRelease(Keyboard.getEventCharacter(), Keyboard.getEventKey());

		super.handleKeyboard();
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	public void preventMouseButtonWithMods(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		if (selectedKeyBinding == null)
			return;

		KeyBindingInterface.from(selectedKeyBinding).setMods(0);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	public void preventInstantModifier(char typedChar, int keyCode, CallbackInfo callback) {
		if (!Modifier.isModifier(keyCode)) {
			boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
			boolean alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

			if ((control || alt || shift) && keyCode > 1 && selectedKeyBinding != null) {
				int mods = 0;
				if (control)
					mods |= Modifier.CTRL;
				if (alt)
					mods |= Modifier.ALT;
				if (shift)
					mods |= Modifier.SHIFT;

				KeyBindingInterface.from(selectedKeyBinding).setMods(mods);

				options.setKeyBindingCode(selectedKeyBinding, keyCode);
				selectedKeyBinding = null;
				time = MinecraftClient.getTime();
				KeyBinding.updateKeysByCode();
			} else {
				if (selectedKeyBinding != null)
					// clear mods - none are held
					KeyBindingInterface.from(selectedKeyBinding).setMods(0);

				return;
			}
		}

		callback.cancel();
	}

	private void keyRelease(char character, int key) {
		if (!Modifier.isModifier(key))
			return;

		if (selectedKeyBinding != null) {
			// clear mods - this is a mod key on its own
			KeyBindingInterface.from(selectedKeyBinding).setMods(0);
			options.setKeyBindingCode(selectedKeyBinding, key);
			selectedKeyBinding = null;
			time = MinecraftClient.getTime();
			KeyBinding.updateKeysByCode();
		}
	}

	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;active:Z"))
	public void resetWithMods(ButtonWidget instance, boolean enabled) {
		if (!enabled) {
			for (KeyBinding binding : options.allKeys) {
				if (KeyBindingInterface.from(binding).getMods() != 0) {
					enabled = true;
					break;
				}
			}
		}

		instance.active = enabled;
	}

	@Redirect(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setCode(I)V"))
	public void resetMods(KeyBinding instance, int keyCode) {
		instance.setCode(keyCode);
		KeyBindingInterface.from(instance).setMods(0);
	}

	@Shadow
	private GameOptions options;
	@Shadow
	public KeyBinding selectedKeyBinding;
	@Shadow
	private long time;

}
