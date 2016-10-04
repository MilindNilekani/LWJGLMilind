#version 450

layout(location=0) in vec3 position;
layout(location=0) in vec2 texCoord; 


out vec2 texCoord0;

uniform mat4 transform;

void main()
{
	//color=vec4(clamp(position,0.0,1.0),1.0);
	gl_Position=transform * vec4(position, 1.0);
	texCoord0=texCoord;
}