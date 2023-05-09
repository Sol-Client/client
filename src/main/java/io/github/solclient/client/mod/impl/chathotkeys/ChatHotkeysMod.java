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

import java.util.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.KeyBindingInterface;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public final class ChatHotkeysMod extends StandardMod {

	@Expose
	final List<Hotkey> entries = new ArrayList<>();
	private Map<Integer, List<Hotkey>> entriesMap;

	// based on how fast i can spam chat without this mod :P
	@Expose
	@Option
	@Slider(min = 0, max = 2, step = 0.1F, format = "sol_client.slider.seconds")
	private float cooldown = 0.4F;
	private long lastSend;

	@Override
	public void init() {
		super.init();
		update();
	}

	void update() {
		entriesMap = new HashMap<>();
		for (Hotkey entry : entries) {
			if (entry.key == 0)
				continue;

			entriesMap.computeIfAbsent(entry.key, ignored -> new ArrayList<>(8)).add(entry);
		}
		entriesMap
				.forEach((key, value) -> value.sort(Comparator.comparingInt(KeyBindingInterface::getMods).reversed()));
	}

	@Override
	protected List<ModOption<?>> createOptions() {
		List<ModOption<?>> result = super.createOptions();
		result.add(new HotkeysOption(this));
		return result;
	}

	@EventHandler
	public void onKeyPressed(KeyPressEvent event) {
		Optional.ofNullable(entriesMap.get(event.key)).ifPresent(keys -> {
			if ((System.currentTimeMillis() - lastSend) < (cooldown * 1000)) {
				mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey("cooldown"))
						.setStyle(new Style().setFormatting(Formatting.RED)));
				return;
			}

			for (Hotkey key : keys) {
				if (!key.areModsPressed())
					continue;

				String message = key.value.trim();
				if (message.isEmpty()) {
					mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey("empty"), key.value)
							.setStyle(new Style().setFormatting(Formatting.RED)));
					continue;
				}

				mc.player.sendChatMessage(message);
				lastSend = System.currentTimeMillis();
				break;
			}
		});
	}

}
