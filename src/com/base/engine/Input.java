package com.base.engine;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input 
{
	public static final int NUM_KEYCODES=256;
	public static final int NUM_MOUSECODES=5;
	private static ArrayList<Integer> currentKeys=new ArrayList<Integer>();
	private static ArrayList<Integer> downKeys=new ArrayList<Integer>();
	private static ArrayList<Integer> upKeys=new ArrayList<Integer>();
	
	private static ArrayList<Integer> currentButtons =new ArrayList<Integer>();
	private static ArrayList<Integer> downButtons =new ArrayList<Integer>();
	private static ArrayList<Integer> upButtons=new ArrayList<Integer>();
	
	public static void update()
	{
		
		downButtons.clear();
		for(int i=0; i<NUM_MOUSECODES;i++)
		{
			if(getMouse(i) && !currentButtons.contains(i))
			{
				downButtons.add(i);
			}
		}
		
		upButtons.clear();
		for(int i=0; i<NUM_MOUSECODES;i++)
		{
			if(!getMouse(i) && currentButtons.contains(i))
			{
				upButtons.add(i);
			}
		}
		
		upKeys.clear();
		for(int i=0; i<NUM_KEYCODES;i++)
		{
			if(!getKey(i) && currentKeys.contains(i))
			{
				upKeys.add(i);
			}
		}
		
		downKeys.clear();
		for(int i=0; i<NUM_KEYCODES;i++)
		{
			if(getKey(i) && !currentKeys.contains(i))
			{
				downKeys.add(i);
			}
		}
		
		currentKeys.clear();
		
		for(int i=0; i<NUM_KEYCODES;i++)
		{
			if(getKey(i))
			{
				currentKeys.add(i);
			}
		}
		
		currentButtons.clear();
		
		for(int i=0; i<NUM_MOUSECODES;i++)
		{
			if(getMouse(i))
			{
				currentButtons.add(i);
			}
		}
	}
	
	public static boolean getKey(int keyCode)
	{
		return Keyboard.isKeyDown(keyCode);
	}
	
	public static boolean getKeyDown(int keyCode)
	{
		return downKeys.contains(keyCode);
	}
	
	public static boolean getKeyUp(int keyCode)
	{
		return upKeys.contains(keyCode);	
	}
	
	public static boolean getMouse(int mouseButton)
	{
		return Mouse.isButtonDown(mouseButton);
	}
	
	public static boolean getMouseDown(int mouseButton)
	{
		return downButtons.contains(mouseButton);
	}
	
	public static boolean getMouseUp(int mouseButton)
	{
		return upButtons.contains(mouseButton);
	}
}
