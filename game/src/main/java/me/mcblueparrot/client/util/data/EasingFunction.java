package me.mcblueparrot.client.util.data;

import me.mcblueparrot.client.lib.penner.easing.Back;
import me.mcblueparrot.client.lib.penner.easing.Bounce;
import me.mcblueparrot.client.lib.penner.easing.Circ;
import me.mcblueparrot.client.lib.penner.easing.Cubic;
import me.mcblueparrot.client.lib.penner.easing.Elastic;
import me.mcblueparrot.client.lib.penner.easing.Expo;
import me.mcblueparrot.client.lib.penner.easing.Linear;
import me.mcblueparrot.client.lib.penner.easing.Quad;
import me.mcblueparrot.client.lib.penner.easing.Quart;
import me.mcblueparrot.client.lib.penner.easing.Quint;
import me.mcblueparrot.client.lib.penner.easing.Sine;
import net.minecraft.client.resources.I18n;

public enum EasingFunction {
	LINEAR,
	QUAD,
	CUBIC,
	QUART,
	QUINT,
	EXPO,
	SINE,
	CIRC,
	BACK,
	BOUNCE,
	ELASTIC;

	@Override
	public String toString() {
		return I18n.format("sol_client.easing." + name().toLowerCase());
	}

	public float ease(float t, float b, float c, float d) {
		switch(this) {
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
