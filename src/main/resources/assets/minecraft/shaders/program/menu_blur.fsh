#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 direction;
uniform float radius;
uniform float radiusMultiplier;

void main() {
	float multipliedRadius = floor(radius * radiusMultiplier);

	vec4 blurred = vec4(0.0);
	float totalStrength = 0.0;
	float totalAlpha = 0.0;
	float totalSamples = 0.0;

	for(float r = -multipliedRadius; r <= multipliedRadius; r += 1.0) {
		vec4 sample = texture2D(DiffuseSampler, texCoord + oneTexel * r * direction);

		// Accumulate average alpha
		totalAlpha = totalAlpha + sample.a;
		totalSamples = totalSamples + 1.0;

		// Accumulate smoothed blur
		float strength = 1.0 - abs(r / multipliedRadius);
		totalStrength = totalStrength + strength;
		blurred = blurred + sample;
	}

	gl_FragColor = vec4(blurred.rgb / (multipliedRadius * 2.0 + 1.0), totalAlpha);
}
