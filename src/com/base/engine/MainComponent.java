package com.base.engine;

public class MainComponent 
{	
	public static final int HEIGHT=1440;
	public static final int WIDTH=2560;
	public static final String TITLE="Harambe's Revenge";
	public static final double FRAME_CAP=5000.0;
	
	private Game game;
	
	private boolean isRunning;
	
	public MainComponent()
	{
		RenderUtil.initGraphics();
		isRunning=false;
		game=new Game();
	}
	
	public void start()
	{
		if(isRunning)
			return;
		
		run();
	}
	
	public void stop()
	{
		if(!isRunning)
			return;
		
		isRunning=false;
	}
	
	private void run()
	{
		isRunning=true;
		
		int frames=0;
		long frameCounter=0;
		final double frameTime=1.0/FRAME_CAP;
		
		long lastTime=Time.getTime();
		double unprocessedTime=0;
		
		while(!Window.isCloseRequested())
		{
			boolean render=false;
			
			long startTime=Time.getTime();
			long passedTime=startTime-lastTime;
			lastTime=startTime;
			
			unprocessedTime+=passedTime/(double)Time.SECOND;
			frameCounter+=passedTime;
			
			while(unprocessedTime>frameTime)
			{
				render=true;
				unprocessedTime-=frameTime;
				if(Window.isCloseRequested())
				{
					stop();
				}
				
				Time.setDelta(frameTime);
				Input.update();
				game.input();
				game.update();
				
				if(frameCounter>=Time.SECOND)
				{
					//System.out.println(frames);
					frames=0;
					frameCounter=0;
				}
			}
		
			
			if(render)
			{
				render();
				frames++;
			}
			else
			{
				try 
				{
					Thread.sleep(1);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		clear();
	}
	
	private void render()
	{
		RenderUtil.clearScreen();
		game.render();
		Window.render();
	}
	
	private void clear()
	{
		Window.destroy();
	}
	
	public static void main(String[] arg)
	{
		Window.createWindow(HEIGHT, WIDTH, TITLE);
		
		MainComponent mc=new MainComponent();
		
		mc.start();
	}
}
