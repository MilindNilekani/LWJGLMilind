package com.base.engine;

public class PointLight
{
	private Light light;
	private Attenuation atten;
	private Vector3f position;
	
	public PointLight(Light light, Attenuation atten, Vector3f position)
	{
		this.light=light;
		this.atten=atten;
		this.position=position;
	}
	
	public Light getLight() {
		return light;
	}
	public void setLight(Light light) {
		this.light = light;
	}
	public Attenuation getAtten() {
		return atten;
	}
	public void setAtten(Attenuation atten) {
		this.atten = atten;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
}
