package net.marc.plunder;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread
{
	private static final String TAG = MainThread.class.getSimpleName();
	
	private final static int MAX_FPS = 50;
	private final static int MAX_FRAME_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000/MAX_FPS;
	
	private DecimalFormat df = new DecimalFormat("0.##");// 2dp
	private final static int STAT_INTERVAL = 1000; //time
	private final static int FPS_HISTORY_NR = 10; // to calc average;
	private long lastStatusStore =0;
	private long statusIntervalTimer = 01;	// status time counter
	private long totalFramesSkipped = 01;
	private long framesSkippedPerStatCycle =01; // frames skipped in 1 sec
	
	private int frameCountPerStatCycle =0; // rendered frames in cycle
	private long totalFrameCount =01;
	private double fpsStore[];//array of past readings
	private long statsCount =0;
	private double averageFps =0.0;
	
	private boolean running;
	private SurfaceHolder surfaceHolder;
	private MainGamePanel gamePanel;
	
	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	public boolean getRunning(){
		return running;
	}
	
	@Override
	public void run()
	{
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		
		initTimingElements();
		
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		
		sleepTime = 0;
		
		while (running)
		{
			canvas= null;

			try
			{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{
					beginTime = System.currentTimeMillis();
					framesSkipped=0;
					//update game state
					this.gamePanel.update();
					//draws the canvas on panel
					if (running){this.gamePanel.render(canvas);}
					// calc how long cycle took
					timeDiff = System.currentTimeMillis()-beginTime;
					//calc sleep time
					sleepTime= (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0)
					{
						try{
							//sleepThread
							Thread.sleep(sleepTime);
						} catch(InterruptedException e){}
					}
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
					{
						// catch up - update without rendering
						this.gamePanel.update();
						// add framePeriod to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					
					if (framesSkipped > 0){
						//Log.d(TAG, "Skipped:" + framesSkipped);
					}
					//for stats
					//framesSkippedPerStatCycle += framesSkipped;
					storeStats();	//store gathered stats
				}
			}
			finally
			{
				// in case of an exception the surface is not left in an inconsistent state
				if (canvas !=null)
				{
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	public void stopThread()
	{
		synchronized(this){
			 
			this.notify();
			boolean retry = true;
			setRunning(false);
			 
			while (retry) {
				try {
					 	join(); //stops thread
					 	retry = false;
				 	}
				 catch (InterruptedException e) {
				 }
			 }
		 }
	}

	
	private void initTimingElements()
	{
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i =0; i< FPS_HISTORY_NR; i++){
			fpsStore[i] =0.0;
		}
		Log.d(TAG+".initTimingElements()", "Timeing elements for stats initialised");
	}
	
	private void storeStats()
	{
		frameCountPerStatCycle++;
		totalFrameCount++;
		
		statusIntervalTimer += (System.currentTimeMillis()- statusIntervalTimer);
		
		if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL)
		{
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL/1000));// calc actual frames /statCycle
			fpsStore[(int)statsCount%FPS_HISTORY_NR] = actualFps; // stores latest fps
			statsCount++;	//note another stat cycle
			
			double totalFps =0.0;
			
			for (int i=0;i<FPS_HISTORY_NR; i++){	//sum stored fps
				totalFps += fpsStore[i];
			}
			
			if (statsCount< FPS_HISTORY_NR){	//calc average
				averageFps = totalFps/statsCount;
			} else{
				averageFps = totalFps/FPS_HISTORY_NR;
			}
			
			totalFramesSkipped += framesSkippedPerStatCycle; // saving total frames skipped
			framesSkippedPerStatCycle =0;
			statusIntervalTimer =0;
			frameCountPerStatCycle =0;
			
			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
			//Log.d(TAG, "Average FPS:" + df.format(averageFps));
			gamePanel.setAvgFps("FPS: " + df.format(averageFps));
		}
	}
	
}