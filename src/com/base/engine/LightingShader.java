package com.base.engine;

public class LightingShader extends Shader 
{
	private static final int MAX_POINT_LIGHTS=4;
	private static final LightingShader instance=new LightingShader();
	
	public static LightingShader getInstance()
	{
		return instance;
	}
	
	private static Vector3f ambientLight=new Vector3f(0.2f,0.2f,0.2f);
	private static DirectionalLight directionalLight=new DirectionalLight(new Light(new Vector3f(1,1,1),0),new Vector3f(0,0,0));
	private static PointLight[] pointLights=new PointLight[]{};
	public LightingShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("lightingVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("lightingFragment.fs"));
		compileShader();
		
		addUniform("transform");
		addUniform("transformProjected");
		addUniform("baseColor");
		addUniform("ambientLight");
		addUniform("dlight.light.color");
		addUniform("dlight.light.intensity");
		addUniform("dlight.direction");
		addUniform("reflectionIntensity");
		addUniform("reflection_spreadConeIntensity");
		addUniform("eyePos");
		for(int i=0;i<MAX_POINT_LIGHTS;i++)
		{
			addUniform("pointLights[" + i + "].light.color");
			addUniform("pointLights[" + i + "].light.intensity");
			addUniform("pointLights[" + i + "].atten.constant");
			addUniform("pointLights[" + i + "].atten.linear");
			addUniform("pointLights[" + i + "].atten.exponent");
			addUniform("pointLights[" + i + "].position");
		}
	}
	

	public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material)
	{
		if(material.getTexture()!=null)
			material.getTexture().bind();
		else
			RenderUtil.unbindTextures();
		setUniform("transformProjected", projectedMatrix);
		setUniform("transform", worldMatrix);
		setUniform("baseColor", material.getColor());
		setUniform("ambientLight", ambientLight);
		setUniform("dlight", directionalLight);
		setUniformf("reflectionIntensity", material.getReflectionIntensity());
		setUniformf("reflection_spreadConeIntensity",material.getReflection_spreadConeIntensity());
		setUniform("eyePos", Transform.getCamera().getPos());
		for(int i=0;i<pointLights.length;i++)
		{
			setUniform("pointLights[" + i + "]", pointLights[i]);
		}
	}
	
	public static void setPointLight(PointLight[] pointLights)
	{
		if(pointLights.length > MAX_POINT_LIGHTS)
		{
			System.err.println("Too many point lights. Max allowed is " + MAX_POINT_LIGHTS);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		LightingShader.pointLights=pointLights;
	}


	public static Vector3f getAmbientLight() {
		return ambientLight;
	}


	public static void setAmbientLight(Vector3f ambientLight) {
		LightingShader.ambientLight = ambientLight;
	}
	
	public static void setDirectionalLight(DirectionalLight directionalLight) {
		LightingShader.directionalLight = directionalLight;
	}
	
	public void setUniform(String uniformName, Light light)
	{
		setUniform(uniformName+".color", light.getColor());
		setUniformf(uniformName+".intensity", light.getIntensity());
	}
	
	public void setUniform(String uniformName, DirectionalLight dlight)
	{
		setUniform(uniformName + ".light", dlight.getLight());
		setUniform(uniformName + ".direction", dlight.getDirection());
	}
	
	public void setUniform(String uniformName, PointLight pointLight)
	{
		setUniform(uniformName + ".light", pointLight.getLight());
		setUniformf(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
		setUniform(uniformName +".position", pointLight.getPosition());
	}

}
