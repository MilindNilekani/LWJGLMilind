package com.base.engine;

public class LightingShader extends Shader 
{
private static final LightingShader instance=new LightingShader();
	
	public static LightingShader getInstance()
	{
		return instance;
	}
	
	private static Vector3f ambientLight=new Vector3f(0.2f,0.2f,0.2f);
	private static DirectionalLight directionalLight=new DirectionalLight(new Light(new Vector3f(1,1,1),0),new Vector3f(0,0,0));
	
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

}
