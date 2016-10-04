#version 450

in vec2 texCoord0;

uniform sampler2D sampler;
uniform vec3 ambientLight;
uniform vec3 baseColor;

void main()
{
	vec4 texColor=texture2D(sampler,texCoord0.xy);
	
	vec4 totalLight=vec4(ambientLight,1);
	
	vec4 color=vec4(baseColor,1);
	
	if(texColor!=vec4(0,0,0,0))
		color*=texColor;
	
	gl_FragColor=color * totalLight;
}