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

package io.github.solclient.client.mod.impl.hud.armour;

import net.minecraft.client.resource.language.I18n;

public enum DurabilityDisplay {
	OFF, FRACTION, REMAINING, PERCENTAGE;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.armour.option.durability." + name().toLowerCase());
	}

	public int getWidth() {
		switch (this) {
			case FRACTION:
				return 67;
			case REMAINING:
				return 29;
			case PERCENTAGE:
				return 30;
			default:
				return 0;
		}
	}

}
