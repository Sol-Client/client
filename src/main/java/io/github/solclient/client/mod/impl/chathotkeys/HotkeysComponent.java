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

package io.github.solclient.client.mod.impl.chathotkeys;

import java.util.List;

import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;

final class HotkeysComponent extends ListComponent {

	private final List<Hotkey> entries;
	private final ChatHotkeysMod mod;

	public HotkeysComponent(ChatHotkeysMod mod, HotkeysOption option) {
		super(Alignment.END);
		this.mod = mod;
		entries = option.getValue();
		updateUi();
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, getSubComponents().size() * 25 - 5);
	}

	public void updateUiAndMap() {
		updateUi();
		updateMap();
	}

	public void updateMap() {
		mod.update();
	}

	public void updateUi() {
		clear();

		ButtonComponent addButton = new ButtonComponent("", Theme.button(), Theme.fg()).width(20).withIcon("plus")
				.onClick((info, button) -> {
					if (button != 0)
						return false;

					MinecraftUtils.playClickSound(true);
					entries.add(new Hotkey(0, 0, ""));
					updateUiAndMap();
					return true;
				});

		Component actions = Component.withBounds(Controller.of(Rectangle.ofDimensions(230, 20)));
		actions.add(new LabelComponent(mod.getTranslationKey("option.hotkeys")),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE));

		if (!entries.isEmpty()) {
			actions.add(new ButtonComponent(mod.getTranslationKey("clear"), Theme.danger(), Theme.fg()).width(45)
					.withIcon("clear").onClick((info, button) -> {
						if (button != 0)
							return false;

						MinecraftUtils.playClickSound(true);
						entries.clear();
						((ScrollListComponent) getParent()).snapTo(0);
						updateUiAndMap();
						return true;
					}), new AlignedBoundsController(Alignment.END, Alignment.START));
		} else
			actions.add(addButton, new AlignedBoundsController(Alignment.END, Alignment.START));

		add(actions);

		entries.forEach(entry -> add(new HotkeyComponent(this, entry)));

		if (!entries.isEmpty())
			add(addButton);
	}

	public void remove(Hotkey hotkey) {
		entries.remove(hotkey);
		updateUiAndMap();
	}

	public boolean isConflicting(KeyBindingInterface binding) {
		return isConflicting((Hotkey) binding);
	}

	public boolean isConflicting(Hotkey hotkey) {
		if (hotkey.key == 0)
			return false;

		return entries.stream().filter(key -> key != hotkey)
				.anyMatch(key -> key.key == hotkey.key && key.mods == hotkey.mods);
	}

}
