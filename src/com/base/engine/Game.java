package com.base.engine;

public class Game 
{
	private static Level level;
	private Player player;

	public Game()
	{
		level=new Level("levelTest.png","wall.png","floor.png","ceiling.png", "grafitti.png", "poster.png");
	}
	
	public void input()
	{
		level.input();
		
	}
	
	public void update()
	{
		level.update();
		
	}
	
	public void render()
	{
		level.render();
		
	}
	
	public static Level getLevel()
	{
		return level;
	}
}
