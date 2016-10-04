package com.base.engine;

public class Vector3f 
{
	private float x;
	private float y;
	private float z;
	
	public Vector3f(float x, float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
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

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public float length()
	{
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public float dot(Vector3f other)
	{
		return x*other.getX() + y *other.getY()+z*other.getZ();
	}
	
	public Vector3f normalizeIntoUnitVector()
	{
		float length=length();
		x=x/length;
		y=y/length;
		z=z/length;
		return this;
	}
	
	public Vector3f rotate(float angle, Vector3f axis)
	{
		float sinHalfAngle=(float)Math.sin(Math.toRadians(angle/2));
		float cosHalfAngle=(float)Math.cos(Math.toRadians(angle/2));
		
		float rX=axis.getX() * sinHalfAngle;
		float rY=axis.getY()* sinHalfAngle;
		float rZ=axis.getZ()* sinHalfAngle;
		float rW=cosHalfAngle;
		
		Quaternion rotation=new Quaternion(rX, rY,rZ,rW);
		Quaternion conjugate=rotation.conjugate();
		
		Quaternion w=rotation.multiply(this).multiply(conjugate);
		
		x=w.getX();
		y=w.getY();
		z=w.getZ();
		
		return this;
	}
	
	public Vector3f add(Vector3f other)
	{
		return new Vector3f(x+other.getX(), y+other.getY(),z+other.getZ());
	}
	
	public Vector3f add(float c)
	{
		return new Vector3f(x+c,y+c,z+c);
	}
	
	public Vector3f subtract(Vector3f other)
	{
		return new Vector3f(x-other.getX(), y-other.getY(), z-other.getZ());
	}
	
	public Vector3f subtract(float c)
	{
		return new Vector3f(x-c,y-c,z-c);
	}
	
	public Vector3f multiply(Vector3f other)
	{
		return new Vector3f(x*other.getX(), y*other.getY(),z*other.getZ());
	}
	
	public Vector3f multiply(float c)
	{
		return new Vector3f(x*c,y*c,z*c);
	}
	
	public Vector3f divide(Vector3f other)
	{
		return new Vector3f(x/other.getX(), y/other.getY(),z/other.getZ());
	}
	
	public Vector3f divide(float c)
	{
		return new Vector3f(x/c,y/c,z/c);
	}
	
	public Vector2f rotate(float angle)
	{
		return null;
	}
	
	public Vector3f abs()
	{
		return new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	
	public Vector3f cross(Vector3f other)
	{
		float a=y*other.getZ()- z*other.getY();
		float b=z*other.getX()-x*other.getZ();
		float c=x*other.getY()-y*other.getX();
		
		return new Vector3f(a,b,c);
	}

}
