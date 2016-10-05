package com.base.engine;

public class DirectionalLight 
{
	private Light light;
	private Vector3f direction;
	
	public DirectionalLight(Light light, Vector3f direction)
	{
		this.light=light;
		this.direction=direction.normalizeIntoUnitVector();
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

}
