package com.base.engine;

public class Game 
{
	private static Level level;
	private static boolean isRunning;

	public Game()
	{
		level=new Level("level1.png","wall.png","floor.png","ceiling.png", "grafitti.png", "poster.png");
		isRunning=true;
	}
	
	public void input()
	{
			level.input();
	}
	
	public void update()
	{
		if(isRunning)
			level.update();
	}
	
	public void render()
	{
		if(isRunning)
			level.render();
	}
	
	public static Level getLevel()
	{
		return level;
	}
	
	public static void setIsRunning(boolean val)
	{
		isRunning=val;
	}
}
