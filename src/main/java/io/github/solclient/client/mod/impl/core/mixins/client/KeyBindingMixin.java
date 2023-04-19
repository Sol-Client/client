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

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.extension.KeyBindingExtension;
import lombok.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.collection.IntObjectStorage;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements KeyBindingExtension {

	@Getter
	@Setter
	private int mods;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IntObjectStorage;set(ILjava/lang/Object;)V"))
	public void rewireAdd(IntObjectStorage<?> instance, int key, Object value) {
		add((KeyBinding) (Object) this);
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void onKeyPressed(int keyCode) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) KEY_MAP.get(keyCode);
			if (keybindings == null)
				return;

			for (KeyBindingExtension keybinding : keybindings) {
				if (!keybinding.areModsPressed())
					continue;

				keybinding.increaseTimesPressed();
			}
		}
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void setKeyPressed(int keyCode, boolean pressed) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) KEY_MAP.get(keyCode);
			if (keybindings == null)
				return;

			for (KeyBindingExtension keybinding : keybindings) {
				if (pressed && !keybinding.areModsPressed())
					continue;

				keybinding.setPressed(pressed);
			}
		}
	}

	@Overwrite
	public static void updateKeysByCode() {
		KEY_MAP.clear();

		for (KeyBinding keybinding : KEYS)
			add(keybinding);
	}

	private static void add(KeyBinding keybinding) {
		List<KeyBinding> existing = KEY_MAP.get(keybinding.getCode());
		if (existing == null) {
			LinkedList<KeyBinding> list = new LinkedList<>();
			list.add(keybinding);
			KEY_MAP.set(keybinding.getCode(), list);
		} else
			existing.add(keybinding);
	}

	@Override
	public void increaseTimesPressed() {
		timesPressed++;
	}

	@Accessor
	public abstract void setPressed(boolean pressed);

	// haha naughty!
	@Shadow
	private static @Final IntObjectStorage<List<KeyBinding>> KEY_MAP;
	@Shadow
	private static @Final List<KeyBinding> KEYS;

	@Shadow
	private int timesPressed;

}
