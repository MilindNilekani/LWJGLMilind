package com.base.engine;

public class Game 
{
	private static Level level;
	private Player player;
	private Enemy enemy;
	public Game()
	{
		level=new Level("levelTest.png","wall.png","floor.png","ceiling.png", "grafitti.png", "poster.png");
		player=new Player(new Vector3f(7f,0.4375f,7f));
		Transform enemyT=new Transform();
		enemyT.setTranslation(new Vector3f(8f,0,8f));
		enemy=new Enemy(enemyT);
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
		enemy.render();
	}
	
	public static Level getLevel()
	{
		return level;
	}
}
