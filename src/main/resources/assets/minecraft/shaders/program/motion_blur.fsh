#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float percent;

void main() {
	gl_FragColor = vec4(mix(texture2D(DiffuseSampler, texCoord), texture2D(PrevSampler, texCoord), percent).xyz, 1);
}
