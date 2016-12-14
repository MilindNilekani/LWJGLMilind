package com.base.engine;


import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioUtil
{
	private static final float AUDIO_VOLUME = -5.0f;
	private static final float DECAY_FACTOR = 0.12f;
	
	public static void playAudio(Clip clip, float distance)
	{
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

		float volumeAmount = AUDIO_VOLUME - (distance * distance * DECAY_FACTOR);

		if(volumeAmount < -80)
			volumeAmount = -80;

		volume.setValue(volumeAmount);

		if(clip.isRunning())
			clip.stop();

		clip.setFramePosition(0);
		clip.start();
	}
	
	public static void loopAudio(Clip clip)
	{
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public static void stopAudio(Clip clip)
	{
		clip.stop();
	}
}