package com.base.engine;

public class PointLight
{
	private Light light;
	private Attenuation atten;
	private Vector3f position;
	private float range;
	
	public PointLight(Light light, Attenuation atten, Vector3f position, float range)
	{
		this.light=light;
		this.atten=atten;
		this.position=position;
		this.range=range;
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

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}
	
}
