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

package io.github.solclient.client.util;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.util.data.Modifier;
import net.minecraft.client.option.KeyBinding;

public interface KeyBindingInterface {

	static KeyBindingInterface from(KeyBinding keybinding) {
		return (KeyBindingInterface) keybinding;
	}

	int getKeyCode();

	void setKeyCode(int keyCode);

	int getMods();

	void setMods(int mods);

	void increaseTimesPressed();

	void setPressed(boolean pressed);

	default boolean areModsPressed() {
		if (getMods() == 0)
			return true;

		boolean control = (getMods() & Modifier.CTRL) != 0;
		boolean alt = (getMods() & Modifier.ALT) != 0;
		boolean shift = (getMods() & Modifier.SHIFT) != 0;

		if (control && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			return false;
		if (alt && !Keyboard.isKeyDown(Keyboard.KEY_LMENU))
			return false;
		if (shift && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			return false;

		return true;
	}

	default String getPrefix() {
		String result = "";

		boolean control = (getMods() & Modifier.CTRL) != 0;
		boolean alt = (getMods() & Modifier.ALT) != 0;
		boolean shift = (getMods() & Modifier.SHIFT) != 0;

		if (shift)
			result = "Shift + " + result;
		if (alt)
			result = "Alt + " + result;
		if (control)
			result = "Ctrl + " + result;

		return result;
	}

}
