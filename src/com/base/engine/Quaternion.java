package com.base.engine;

public class Quaternion 
{
	private float x;
	private float y;
	private float z;
	private float w;
	
	public Quaternion(float x,float y, float z, float w)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}
	
	public float length()
	{
		return(float)Math.sqrt(x*x+y*y+z*z+w*w);
	}
	
	public Quaternion normalizedIntoUnitVector()
	{
		float length=length();
		x=x/length;
		y=y/length;
		z=z/length;
		w=w/length;
		
		return this;
	}
	
	public Quaternion conjugate()
	{
		return new Quaternion(-x,-y,-z,w);
	}
	
	public Quaternion multiply(Quaternion other)
	{
		float a=w*other.getW() - x*other.getX() -y*other.getY() -z*other.getZ();
		float b=x*other.getW() +w*other.getX()+y*other.getZ()-z*other.getY();
		float c=y*other.getW()+w*other.getY()+z*other.getX()-x*other.getZ();
		float d=z*other.getW()+w*other.getZ()+x*other.getY()-y*other.getX();
		return new Quaternion(b,c,d,a);
	}
	
	public Quaternion multiply(Vector3f other)
	{
		float a=-x*other.getX()-y*other.getY()-z*other.getZ();
		float b=w*other.getX()+y*other.getZ()-z*other.getY();
		float c=w*other.getY()+z*other.getX()-x*other.getZ();
		float d=w*other.getZ()+x*other.getY()-y*other.getX();
		
		return new Quaternion(b,c,d,a);
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

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}
}
