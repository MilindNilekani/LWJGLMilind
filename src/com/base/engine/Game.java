package com.base.engine;

import org.lwjgl.input.Keyboard;

public class Game 
{
	public Game()
	{
		
	}
	
	public void input()
	{
		if(Input.getKeyDown(Keyboard.KEY_UP))
				System.out.println("Pressed Up");
		if(Input.getKeyUp(Keyboard.KEY_UP))
				System.out.println("Released Up");
		
		if(Input.getMouseDown(1))
			System.out.println("Pressed Right Mouse");
		if(Input.getMouseUp(1))
			System.out.println("Released Right Mouse");
	}
	
	public void update()
	{
		
	}
	
	public void render()
	{
		
	}
}
