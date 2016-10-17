package com.base.engine;

import org.lwjgl.input.Keyboard;

public class Camera 
{
	public static final Vector3f yAxis=new Vector3f(0,1,0);
	
	private Vector3f pos;
	private Vector3f forward;
	private Vector3f up;
	public Camera()
	{
		this(new Vector3f(0,0,0), new Vector3f(0,0,1), new Vector3f(0,1,0));
	}
	
	public Camera(Vector3f pos, Vector3f forward, Vector3f up)
	{
		this.pos=pos;
		this.forward=forward;
		this.up=up;
		
		up.normalizeIntoUnitVector();
		forward.normalizeIntoUnitVector();
	}
	
	boolean mouseLocked=false;
	Vector2f centerPosition=new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	
	public void input()
	{
		float sen=0.5f;
		float movAmt=(float)(Time.getDelta());
		float rotAmt=(float)(10*Time.getDelta());
		
		if(Input.getKey(Keyboard.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked=false;
		}
		
		if(Input.getMouseDown(0))
		{
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked=true;
		}
		
		if(Input.getKey(Keyboard.KEY_W))
			move(getForward(), movAmt);
		if(Input.getKey(Keyboard.KEY_S))
			move(getForward(), -movAmt);
		if(Input.getKey(Keyboard.KEY_A))
			move(getLeft(), movAmt);
		if(Input.getKey(Keyboard.KEY_D))
			move(getRight(), movAmt);
		
		if(Input.getKey(Keyboard.KEY_UP))
			rotateX(-rotAmt);
		if(Input.getKey(Keyboard.KEY_DOWN))
			rotateX(rotAmt);
		if(Input.getKey(Keyboard.KEY_LEFT))
			rotateY(-rotAmt);
		if(Input.getKey(Keyboard.KEY_RIGHT))
			rotateY(rotAmt);
			
		
		if(mouseLocked)
		{
			Vector2f deltaPos=Input.getMousePosition().subtract(centerPosition);
			
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;
			
			if(rotY)
				rotateY(deltaPos.getX()*sen);
			if(rotX)
				rotateX(-deltaPos.getY()*sen);
			if(rotX || rotY)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
		
	}
	
	public void move(Vector3f dir, float value)
	{
		pos=pos.add(dir.multiply(value));
	}
	
	public Vector3f getLeft()
	{
		Vector3f left=forward.cross(up);
		left.normalizeIntoUnitVector();
		return left;
	}
	
	public Vector3f getRight()
	{
		Vector3f right=up.cross(forward);
		right.normalizeIntoUnitVector();
		return right;
	}
	
	public void rotateX(float angle)
	{
		Vector3f Haxis=yAxis.cross(forward);
		Haxis.normalizeIntoUnitVector();
		
		forward.rotate(angle, Haxis);
		forward.normalizeIntoUnitVector();
		
		up=forward.cross(Haxis);
		up.normalizeIntoUnitVector();
	}
	
	public void rotateY(float angle)
	{
		Vector3f Haxis=yAxis.cross(forward);
		Haxis.normalizeIntoUnitVector();
		
		forward.rotate(angle, yAxis);
		forward.normalizeIntoUnitVector();
		
		up=forward.cross(Haxis);
		up.normalizeIntoUnitVector();
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getForward() {
		return forward;
	}

	public void setForward(Vector3f forward) {
		this.forward = forward;
	}

	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}
}
