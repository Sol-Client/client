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

package io.github.solclient.client.mod.option.impl;

import java.util.function.*;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;

public class KeyBindingOption extends ModOption<KeyBinding> {

	public KeyBindingOption(String name, ModOptionStorage<KeyBinding> storage) {
		super(name, storage);
	}

	@Override
	public void setValue(KeyBinding value) {
		throw new UnsupportedOperationException("Cannot reassign keybinding");
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		ButtonComponent editButton = createEditButton(KeyBindingInterface.from(getValue()), 16, true, MinecraftUtils::isConflicting, () -> {
			MinecraftClient.getInstance().options.save();
			KeyBinding.updateKeysByCode();
		});
		container.add(editButton, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		return container;
	}

	public static ButtonComponent createEditButton(KeyBindingInterface binding, int height, boolean mouse, Predicate<KeyBindingInterface> conflicts, Runnable callback) {
		boolean[] listening = new boolean[1];

		ButtonComponent result = new ButtonComponent(
				(component, defaultText) -> binding.getPrefix()
						+ GameOptions.getFormattedNameForKeyCode(binding.getKeyCode()),
				Theme.button(), (component, defaultColour) -> {
					if (listening[0])
						return new Colour(255, 255, 85);
					else if (conflicts.test(binding))
						return new Colour(255, 85, 85);

					return Theme.getCurrent().fg;
				}).width(45).height(height);

		result.onClick((info, button) -> {
			if (button == 0) {
				MinecraftUtils.playClickSound(true);

				Component root = result.getScreen().getRoot();
				GameOptions options = MinecraftClient.getInstance().options;

				Consumer<Integer> setter = code -> {
					if (binding instanceof KeyBinding)
						options.setKeyBindingCode((KeyBinding) binding, code);
					else
						binding.setKeyCode(code);
				};

				Runnable postSet = () -> {
					listening[0] = false;
					callback.run();
					root.onKeyPressed(null);
					root.onKeyReleased(null);
					root.onClickAnwhere(null);
				};

				listening[0] = true;
				root.onClickAnwhere((ignoredInfo, pressedButton) -> {
					if (mouse) {
						if (binding instanceof KeyBinding)
							options.setKeyBindingCode((KeyBinding) binding, pressedButton - 100);
						else
							binding.setKeyCode(pressedButton - 100);
						binding.setMods(0);
					}
					postSet.run();
					return true;
				});

				root.onKeyPressed((ignored, key, character) -> {
					if (Modifier.isModifier(key))
						return false;
					int mods = 0;

					if (key == 1)
						setter.accept(0);
					else if (key != 0) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
							mods |= Modifier.CTRL;
						if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
							mods |= Modifier.ALT;
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
							mods |= Modifier.SHIFT;

						setter.accept(key);
					} else if (character > 0)
						setter.accept(character + 256);

					binding.setMods(mods);
					postSet.run();
					return true;
				});

				root.onKeyReleased((ignored, key, character) -> {
					if (!Modifier.isModifier(key))
						return false;

					setter.accept(key);
					binding.setMods(0);

					postSet.run();
					return true;
				});
				return true;
			}

			return false;
		});

		return result;
	}

}
