package com.base.engine;

import javax.sound.sampled.Clip;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	private static final Clip BACKGROUND_MUSIC=ResourceLoader.loadAudio("d_e1m1.mid");
	private static int levelNum=0;
	private static final int NUMBERLEVELS=2;

	public Game() throws InterruptedException
	{
		loadNextLevel();
	}
	
	public void input()
	{
			level.input();
	}
	
	public void update()
	{
		if(isRunning)
			level.update();
		else
		{
			AudioUtil.stopAudio(BACKGROUND_MUSIC);
		}
	}
	
	public void render()
	{
		if(isRunning)
			level.render();
	}
	
	public static void loadNextLevel()
	{
		levelNum++;
		if(levelNum<=NUMBERLEVELS)
		{
			level=new Level("level" + levelNum + ".png");
			isRunning=true;
			Transform.setCamera(level.getPlayer().getCamera());
			Transform.setProjection(70, 0.01f, 1000f, Window.getWidth(), Window.getHeight());
			AudioUtil.playAudio(BACKGROUND_MUSIC,10);
			AudioUtil.loopAudio(BACKGROUND_MUSIC);
		}
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
