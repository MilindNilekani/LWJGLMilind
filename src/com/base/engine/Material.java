package com.base.engine;

public class Material
{
	private Texture texture;
	private Vector3f color;
	private float reflectionIntensity;
	private float reflection_spreadConeIntensity;
	
	public Material(Texture texture)
	{
		this(texture, new Vector3f(1,1,1));
	}
	public Material(Texture texture, Vector3f color)
	{
		this(texture,color, 2,32);
	}
	
	public Material(Texture texture, Vector3f color, float reflectionIntensity, float reflection_spreadConeIntensity)
	{
		this.texture=texture;
		this.color=color;
		this.reflectionIntensity=reflectionIntensity;
		this.reflection_spreadConeIntensity=reflection_spreadConeIntensity;
	}
	
	/*public Material(Texture texture, float reflectionIntensity, float reflection_spreadConeIntensity)
	{
		this.texture=texture;
		this.color=new Vector3f(1,1,1);
		this.reflectionIntensity=reflectionIntensity;
		this.reflection_spreadConeIntensity=reflection_spreadConeIntensity;
	}*/

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	public float getReflectionIntensity() {
		return reflectionIntensity;
	}
	public void setReflectionIntensity(float reflectionIntensity) {
		this.reflectionIntensity = reflectionIntensity;
	}
	public float getReflection_spreadConeIntensity() {
		return reflection_spreadConeIntensity;
	}
	public void setReflection_spreadConeIntensity(float reflection_spreadConeIntensity) {
		this.reflection_spreadConeIntensity = reflection_spreadConeIntensity;
	}
}
