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

package io.github.solclient.client.mod.impl.quickplay.database;

import com.google.gson.JsonObject;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayOption;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

public class QuickPlayGameMode extends QuickPlayOption {

	@Getter
	private final QuickPlayGame parent;
	@Getter
	private final String name;
	@Getter
	private final String command;

	public QuickPlayGameMode(QuickPlayGame parent, JsonObject object) {
		this.parent = parent;
		name = object.get("name").getAsString();
		String command = object.get("command").getAsString();

		if (command.equals("/quickplay limbo")) {
			command = "/achat §";
		} else if (command.equals("/quickplay delivery")) {
			command = "/delivery";
		}

		this.command = command;
	}

	public String getFullId() {
		return parent.getId() + "." + command;
	}

	@Override
	public String getText() {
		if (parent.getModes().size() == 1)
			return parent.getName();

		return Formatting.strip(parent.getName() + ": " + name);
	}

	@Override
	public void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod) {
		mod.playGame(this);
	}

	@Override
	public ItemStack getIcon() {
		return parent.getIcon();
	}

}
