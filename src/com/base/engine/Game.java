package com.base.engine;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	private static ArrayList<Clip> playlist;
	private static int levelNum=0;
	private static final int NUMBERLEVELS=3;
	
	public static Clip getCurrentClip()
	{
		return playlist.get(levelNum-1);
	}
	
	public Game()
	{
		playlist=new ArrayList<Clip>();
		for(int i=0;i<NUMBERLEVELS-1;i++)
		{
			playlist.add(ResourceLoader.loadAudio("d_e1m"+(i+1)+".mid"));
		}
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
			AudioUtil.playAudio(playlist.get(levelNum-1),10);
			AudioUtil.loopAudio(playlist.get(levelNum-1));
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
