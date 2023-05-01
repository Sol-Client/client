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

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.util.MinecraftUtils;
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

		boolean[] listening = new boolean[1];
		ButtonComponent editButton = new ButtonComponent(
				(component, defaultText) -> KeyBindingExtension.from(getValue()).getPrefix()
						+ GameOptions.getFormattedNameForKeyCode(getValue().getCode()),
				Theme.button(), (component, defaultColour) -> {
					if (listening[0])
						return new Colour(255, 255, 85);
					else if (MinecraftUtils.isConflicting(getValue()))
						return new Colour(255, 85, 85);

					return Theme.getCurrent().fg;
				}).width(45).height(16);
		container.add(editButton, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		editButton.onClick((info, button) -> {
			if (button == 0) {
				MinecraftUtils.playClickSound(true);

				Component root = container.getScreen().getRoot();
				GameOptions options = MinecraftClient.getInstance().options;

				Runnable postSet = () -> {
					listening[0] = false;
					MinecraftClient.getInstance().options.save();
					KeyBinding.updateKeysByCode();
					root.onKeyPressed(null);
					root.onKeyReleased(null);
					root.onClickAnwhere(null);
				};

				listening[0] = true;
				root.onClickAnwhere((ignoredInfo, pressedButton) -> {
					MinecraftClient.getInstance().options.setKeyBindingCode(getValue(), pressedButton - 100);
					KeyBindingExtension.from(getValue()).setMods(0);
					postSet.run();
					return true;
				});

				root.onKeyPressed((ignored, key, character) -> {
					if (Modifier.isModifier(key))
						return false;
					int mods = 0;

					if (key == 1)
						options.setKeyBindingCode(getValue(), 0);
					else if (key != 0) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
							mods |= Modifier.CTRL;
						if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
							mods |= Modifier.ALT;
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
							mods |= Modifier.SHIFT;

						options.setKeyBindingCode(getValue(), key);
					} else if (character > 0)
						options.setKeyBindingCode(getValue(), character + 256);

					KeyBindingExtension.from(getValue()).setMods(mods);
					postSet.run();
					return true;
				});

				root.onKeyReleased((ignored, key, character) -> {
					if (!Modifier.isModifier(key))
						return false;

					options.setKeyBindingCode(getValue(), key);
					KeyBindingExtension.from(getValue()).setMods(0);

					postSet.run();
					return true;
				});
				return true;
			}

			return false;
		});

		return container;
	}

}
