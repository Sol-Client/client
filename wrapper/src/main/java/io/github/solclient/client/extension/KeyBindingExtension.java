package io.github.solclient.client.extension;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.util.data.Modifier;
import net.minecraft.client.option.KeyBinding;

public interface KeyBindingExtension {

	static KeyBindingExtension from(KeyBinding keybinding) {
		return (KeyBindingExtension) keybinding;
	}

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
