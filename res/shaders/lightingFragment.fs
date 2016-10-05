#version 450

in vec2 texCoord0;
in vec3 normal0;


struct Light
{
	vec3 color;
	float intensity;
};

struct DirectionalLight
{
	Light light;
	vec3 direction;
};

uniform sampler2D sampler;
uniform vec3 ambientLight;
uniform vec3 baseColor;

uniform DirectionalLight dlight;

vec4 calculateLight(Light light, vec3 direction, vec3 normal)
{
	float diffuse=dot(normal, -direction);
	
	vec4 diffuseColor=vec4(0,0,0,0);
	
	if(diffuse>0)
	{
			diffuseColor=vec4(light.color,1.0) * light.intensity * diffuse;
	}
	return diffuseColor;
}

vec4 calculateDirectionalLight(DirectionalLight dlight, vec3 normal)
{
	return calculateLight(dlight.light, -dlight.direction, normal);
}

void main()
{
	vec4 texColor=texture2D(sampler,texCoord0.xy);
	
	vec4 totalLight=vec4(ambientLight,1);
	
	vec4 color=vec4(baseColor,1);
	
	if(texColor!=vec4(0,0,0,0))
		color*=texColor;
		
	vec3 normal=normalize(normal0);
	
	totalLight=totalLight+calculateDirectionalLight(dlight, normal);
	
	gl_FragColor=color * totalLight;
}