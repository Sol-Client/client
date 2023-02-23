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

package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import net.minecraft.item.*;

public class BackOption extends QuickPlayOption {

	@Override
	public String getText() {
		return "← Back";
	}

	@Override
	public void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod) {
		palette.back();
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.ARROW);
	}

}
