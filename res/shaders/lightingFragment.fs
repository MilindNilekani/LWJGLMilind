#version 450

const int MAX_POINT_LIGHTS=4;

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;

out vec4 FragColor;

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

struct Attenuation
{
	float constant;
	float linear;
	float exponent;
};

struct PointLight
{
	Light light;
	Attenuation atten;
	vec3 position;
};

uniform sampler2D sampler;
uniform vec3 eyePos;
uniform vec3 ambientLight;
uniform vec3 baseColor;
uniform float reflectionIntensity;
uniform float reflection_spreadConeIntensity; 

uniform DirectionalLight dlight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

vec4 calculateLight(Light light, vec3 direction, vec3 normal)
{
	float diffuse=dot(normal, -direction);
	
	vec4 diffuseColor=vec4(0,0,0,0);
	vec4 reflectColor=vec4(0,0,0,0);
	if(diffuse>0)
	{
			diffuseColor=vec4(light.color,1.0) * light.intensity * diffuse;
			
			vec3 directionToEye=normalize(eyePos-worldPos0);
			vec3 reflectDirection=normalize(reflect(direction, normal));
			
			float reflectFactor=dot(directionToEye,reflectDirection);
			reflectFactor=pow(reflectFactor,reflection_spreadConeIntensity);
			
			if(reflectFactor>0)
			{
				reflectColor=vec4(light.color,1.0)*reflectionIntensity*reflectFactor;
			}
			
	}
	return diffuseColor + reflectColor;
}

vec4 calculateDirectionalLight(DirectionalLight dlight, vec3 normal)
{
	return calculateLight(dlight.light, -dlight.direction, normal);
}

vec4 calculatePointLight(PointLight pointLight, vec3 normal)
{
	vec3 lightDirection=worldPos0 - pointLight.position;
	float distanceFromPoint=length(lightDirection);
	lightDirection=normalize(lightDirection);
	
	vec4 color=calculateLight(pointLight.light, lightDirection, normal);
	
	float attenuation=pointLight.atten.constant + pointLight.atten.linear*distanceFromPoint + pointLight.atten.exponent*distanceFromPoint*distanceFromPoint+0.0000001f;
	
	return color/attenuation;
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
	
	for(int i=0;i<MAX_POINT_LIGHTS;i++)
	{
		totalLight=totalLight+calculatePointLight(pointLights[i], normal);
	}
	
	FragColor=color * totalLight;
}