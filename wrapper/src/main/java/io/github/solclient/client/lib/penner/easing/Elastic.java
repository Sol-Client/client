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

public class Elastic {

	public static float easeIn(float t, float b, float c, float d) {
		if (t == 0)
			return b;
		if ((t /= d) == 1)
			return b + c;
		float p = d * .3f;
		float a = c;
		float s = p / 4;
		return -(a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p))
				+ b;
	}

	public static float easeIn(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t == 0)
			return b;
		if ((t /= d) == 1)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4;
		} else {
			s = p / (2 * (float) Math.PI) * (float) Math.asin(c / a);
		}
		return -(a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
	}

	public static float easeOut(float t, float b, float c, float d) {
		if (t == 0)
			return b;
		if ((t /= d) == 1)
			return b + c;
		float p = d * .3f;
		float a = c;
		float s = p / 4;
		return (a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) + c + b);
	}

	public static float easeOut(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t == 0)
			return b;
		if ((t /= d) == 1)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4;
		} else {
			s = p / (2 * (float) Math.PI) * (float) Math.asin(c / a);
		}
		return (a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) + c + b);
	}

	public static float easeInOut(float t, float b, float c, float d) {
		if (t == 0)
			return b;
		if ((t /= d / 2) == 2)
			return b + c;
		float p = d * (.3f * 1.5f);
		float a = c;
		float s = p / 4;
		if (t < 1)
			return -.5f * (a * (float) Math.pow(2, 10 * (t -= 1))
					* (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p)) + b;
		return a * (float) Math.pow(2, -10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) * .5f
				+ c + b;
	}

	public static float easeInOut(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t == 0)
			return b;
		if ((t /= d / 2) == 2)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4;
		} else {
			s = p / (2 * (float) Math.PI) * (float) Math.asin(c / a);
		}
		if (t < 1)
			return -.5f * (a * (float) Math.pow(2, 10 * (t -= 1))
					* (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p)) + b;
		return a * (float) Math.pow(2, -10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p) * .5f
				+ c + b;
	}

}
