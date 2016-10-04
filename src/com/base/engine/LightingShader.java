package com.base.engine;

public class LightingShader extends Shader 
{
private static final LightingShader instance=new LightingShader();
	
	public static LightingShader getInstance()
	{
		return instance;
	}
	
	private static Vector3f ambientLight;
	
	public LightingShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("lightingVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("lightingFragment.fs"));
		compileShader();
		
		addUniform("transform");
		addUniform("baseColor");
		addUniform("ambientLight");
	}
	

	public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material)
	{
		if(material.getTexture()!=null)
			material.getTexture().bind();
		else
			RenderUtil.unbindTextures();
		setUniform("transform", projectedMatrix);
		setUniform("baseColor", material.getColor());
		setUniform("ambientLight", ambientLight);
	}


	public static Vector3f getAmbientLight() {
		return ambientLight;
	}


	public static void setAmbientLight(Vector3f ambientLight) {
		LightingShader.ambientLight = ambientLight;
	}

}
