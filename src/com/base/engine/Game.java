package com.base.engine;

public class Game 
{
	private static Level level;
	private Player player;
	public Game()
	{
		level=new Level("levelTest.png","wallfinal.png","floor.png","ceiling.png", "grafitti.png", "poster.png");
		player=new Player(new Vector3f(7f,0.4375f,7f));
		Transform.setCamera(player.getCamera());
		Transform.setProjection(70, 0.01f, 1000f, Window.getWidth(), Window.getHeight());
	}
	
	public void input()
	{
		level.input();
		player.input();
	}
	
	public void update()
	{
		level.update();
		player.update();
	}
	
	public void render()
	{
		level.render();
		player.render();
	}
	
	public static Level getLevel()
	{
		return level;
	}
}
