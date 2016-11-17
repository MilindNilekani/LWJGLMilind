package com.base.engine;

import javax.sound.sampled.Clip;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	private static final Clip BACKGROUND_MUSIC=ResourceLoader.loadAudio("d_e1m1.mid");

	public Game()
	{
		level=new Level("level1.png");
		isRunning=true;
		AudioUtil.playAudio(BACKGROUND_MUSIC,10);
		AudioUtil.loopAudio(BACKGROUND_MUSIC);
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
			AudioUtil.stopAudio(BACKGROUND_MUSIC);
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
