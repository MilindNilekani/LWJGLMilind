package com.base.engine;

import org.lwjgl.input.Keyboard;

public class Player {
	private Camera camera;
	boolean mouseLocked=false;
	private Vector2f centerPosition=new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movement;
	
	public Player(Vector3f position)
	{
		camera=new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
	}
	
	public void input()
	{
		float sen=0.5f;
		float movAmt=2*(float)(Time.getDelta());
		
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
		
		movement=new Vector3f(0,0,0);
		
		if(Input.getKey(Keyboard.KEY_W))
			movement=movement.add(camera.getForward());//camera.move(camera.getForward(), movAmt);
		if(Input.getKey(Keyboard.KEY_S))
			movement=movement.subtract(camera.getForward());//camera.move(camera.getForward(), -movAmt);
		if(Input.getKey(Keyboard.KEY_A))
			movement=movement.add(camera.getLeft());//camera.move(camera.getLeft(), movAmt);
		if(Input.getKey(Keyboard.KEY_D))
			movement=movement.add(camera.getRight());//camera.move(camera.getRight(), movAmt);
		
		movement.setY(0);
		if(movement.length()>0)
			movement=movement.normalizeIntoUnitVector();
		
		camera.move(movement, movAmt);
		
		if(mouseLocked)
		{
			Vector2f deltaPos=Input.getMousePosition().subtract(centerPosition);
			
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;
			
			if(rotY)
				camera.rotateY(deltaPos.getX()*sen);
			if(rotX)
				camera.rotateX(-deltaPos.getY()*sen);
			if(rotX || rotY)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}
	
	public void update()
	{
	
	}
	
	public void render()
	{
		
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
			

}
