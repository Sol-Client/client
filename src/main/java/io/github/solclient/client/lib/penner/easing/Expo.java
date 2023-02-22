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

package io.github.solclient.client.lib.penner.easing;

public class Expo {

	public static float easeIn(float t, float b, float c, float d) {
		return (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;
	}

	public static float easeOut(float t, float b, float c, float d) {
		return (t == d) ? b + c : c * (-(float) Math.pow(2, -10 * t / d) + 1) + b;
	}

	public static float easeInOut(float t, float b, float c, float d) {
		if (t == 0)
			return b;
		if (t == d)
			return b + c;
		if ((t /= d / 2) < 1)
			return c / 2 * (float) Math.pow(2, 10 * (t - 1)) + b;
		return c / 2 * (-(float) Math.pow(2, -10 * --t) + 2) + b;
	}

}
