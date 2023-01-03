package io.github.solclient.client.lib.penner.easing;

public class Cubic {

	public static float easeIn(float t, float b, float c, float d) {
		return c * (t /= d) * t * t + b;
	}

	public static float easeOut(float t, float b, float c, float d) {
		return c * ((t = t / d - 1) * t * t + 1) + b;
	}

	public static float easeInOut(float t, float b, float c, float d) {
		if ((t /= d / 2) < 1)
			return c / 2 * t * t * t + b;
		return c / 2 * ((t -= 2) * t * t + 2) + b;
	}

}
