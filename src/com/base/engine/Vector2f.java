package com.base.engine;

public class Vector2f 
{
	private float x;
	private float y;
	
	public Vector2f(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
	
	public float length()
	{
		return (float) Math.sqrt(x*x+y*y);
	}
	
	public float dot(Vector2f other)
	{
		return x*other.getX() + y *other.getY();
	}
	
	public Vector2f normalizeIntoUnitVector()
	{
		float length=length();
		x=x/length;
		y=y/length;
		return this;
	}
	
	public Vector2f rotate(float angle)
	{
		double rad=Math.toRadians(angle);
		double cos=Math.cos(rad);
		double sin=Math.sin(rad);
		
		return new Vector2f((float)(x*cos -y * sin),(float)(x*sin + y*cos));
	}
	
	public Vector2f add(Vector2f other)
	{
		return new Vector2f(x+other.getX(), y+other.getY());
	}
	
	public Vector2f add(float c)
	{
		return new Vector2f(x+c,y+c);
	}
	
	public Vector2f subtract(Vector2f other)
	{
		return new Vector2f(x-other.getX(), y-other.getY());
	}
	
	public Vector2f subtract(float c)
	{
		return new Vector2f(x-c,y-c);
	}
	
	public Vector2f multiply(Vector2f other)
	{
		return new Vector2f(x*other.getX(), y*other.getY());
	}
	
	public Vector2f multiply(float c)
	{
		return new Vector2f(x*c,y*c);
	}
	
	public Vector2f divide(Vector2f other)
	{
		return new Vector2f(x/other.getX(), y/other.getY());
	}
	
	public Vector2f divide(float c)
	{
		return new Vector2f(x/c,y/c);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}
