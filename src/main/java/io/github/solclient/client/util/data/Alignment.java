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

package io.github.solclient.client.util.data;

import net.minecraft.util.math.MathHelper;

/**
 * Alignment not aware of its axis.
 */
public enum Alignment {
	/**
	 * Aligned to left or top.
	 */
	START,
	/**
	 * Centred.
	 */
	CENTRE,
	/**
	 * Aligned to right or bottom.
	 */
	END;

	public static Alignment fromNullable(Alignment alignment) {
		if (alignment == null)
			return START;

		return alignment;
	}

	public int getPosition(int areaSize, int objectSize) {
		switch (this) {
			case CENTRE:
				return (areaSize / 2) - (objectSize / 2);
			case END:
				return areaSize - objectSize;
			default:
				return 0;
		}
	}
}
