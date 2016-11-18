#version 450

in vec2 texCoord0;

uniform sampler2D sampler;
uniform vec3 color;

void main()
{
	vec4 texColor=texture2D(sampler,texCoord0.xy);
	
	if(texColor==vec4(0,0,0,0))
		discard;
	else
		gl_FragColor=texColor*vec4(color,1);
}