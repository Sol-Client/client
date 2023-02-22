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

import io.github.solclient.client.lib.penner.easing.*;
import net.minecraft.client.resource.language.I18n;

public enum EasingFunction {
	LINEAR, QUAD, CUBIC, QUART, QUINT, EXPO, SINE, CIRC, BACK, BOUNCE, ELASTIC;

	@Override
	public String toString() {
		return I18n.translate("sol_client.easing." + name().toLowerCase());
	}

	public float ease(float t, float b, float c, float d) {
		switch (this) {
			case LINEAR:
				return Linear.easeNone(t, b, c, d);
			case QUAD:
				return Quad.easeOut(t, b, c, d);
			case CUBIC:
				return Cubic.easeOut(t, b, c, d);
			case QUART:
				return Quart.easeOut(t, b, c, d);
			case QUINT:
				return Quint.easeOut(t, b, c, d);
			case EXPO:
				return Expo.easeOut(t, b, c, d);
			case SINE:
				return Sine.easeOut(t, b, c, d);
			case CIRC:
				return Circ.easeOut(t, b, c, d);
			case BACK:
				return Back.easeOut(t, b, c, d);
			case BOUNCE:
				return Bounce.easeOut(t, b, c, d);
			case ELASTIC:
				return Elastic.easeOut(t, b, c, d);
			default:
				return 0;
		}
	}

}
