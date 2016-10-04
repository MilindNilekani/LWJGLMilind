#version 450

in vec2 texCoord0;

uniform sampler2D sampler;

out vec4 fragColor;

void main()
{
	gl_FragColor=texture2D(sampler,texCoord0.xy);
}