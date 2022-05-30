#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float BlendFactor;

void main() {
	// Copied three letters from a stackoverflow question (mix), but that's all I needed to create motion blur.
	// https://stackoverflow.com/questions/37913286/glsl-motion-blur-post-processing-2-textures-going-to-the-shader-are-the-same

	gl_FragColor = mix(texture2D(DiffuseSampler, texCoord), texture2D(PrevSampler, texCoord), BlendFactor);

	gl_FragColor.w = 1.0;
}
