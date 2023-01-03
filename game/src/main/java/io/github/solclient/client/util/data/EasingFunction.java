package io.github.solclient.client.util.data;

import io.github.solclient.client.lib.penner.easing.*;
import net.minecraft.client.resources.I18n;

public enum EasingFunction {
	LINEAR, QUAD, CUBIC, QUART, QUINT, EXPO, SINE, CIRC, BACK, BOUNCE, ELASTIC;

	@Override
	public String toString() {
		return I18n.format("sol_client.easing." + name().toLowerCase());
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
