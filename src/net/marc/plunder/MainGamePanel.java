package net.marc.plunder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Paint;
import net.marc.plunder.model.AIPlayer;
import net.marc.plunder.model.SatelliteContainer;
import net.marc.plunder.model.Ship;
import net.marc.plunder.model.Probe;
import net.marc.plunder.model.UIOverlay;
import net.marc.plunder.model.XMLHandler;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String TAG = MainThread.class.getSimpleName();
	
	protected float WIDTH_RATIO;
	protected float HEIGHT_RATIO;
	
	protected boolean firstRound;
	
	protected MainThread thread;
	protected String avgFps;
	protected SatelliteContainer satelliteContainer;
	protected Probe probe;
	protected Ship[] players;
	
	protected AIPlayer cpuPlayer;
	protected Boolean aiPlaying;
	
	protected UIOverlay phaseUI;
	protected UIOverlay scoreUI;
	
	protected boolean hide;
	
	protected Picture probePic;
	
	protected XMLHandler xmlHandler;
	
	// instance save values
	protected int activePlayer;
	protected int gamePhase;
	protected String phaseTextOne;
	protected String phaseTextTwo;
	protected int stolen;
	
	//CONSTRUCTORS & INITIATION
	
	public MainGamePanel(Context context)
	{
		super(context);
		//adding callback to the surface holder to intercept events
		
		getHolder().addCallback(this);
		
		//create the game loop thread
		thread = new MainThread(getHolder(), this);
		xmlHandler = new XMLHandler();
		
		
		// make GamePanel focusable so it can handle events
		setFocusable(true);
		
		hide = false;
		activePlayer =0;
		gamePhase = 0;
	}
	
	public MainGamePanel(Context context, AttributeSet attrs) 
	{
		super( context, attrs );
		
		getHolder().addCallback(this);
		
		//create the game loop thread
		thread = new MainThread(getHolder(), this);
		xmlHandler = new XMLHandler();
		
		// make GamePanel focusable so it can handle events
		setFocusable(true);
		
		hide = false;
		activePlayer =0;
		gamePhase = 0;
	}
		 
	public MainGamePanel(Context context, AttributeSet attrs, int defStyle)
	{
		super( context, attrs, defStyle );
		
		getHolder().addCallback(this);
		
		//create the game loop thread
		thread = new MainThread(getHolder(), this);
		xmlHandler = new XMLHandler();
		
		// make GamePanel focusable so it can handle events
		setFocusable(true);
		
		hide = false;
		activePlayer =0;
		gamePhase = 0;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		
		WIDTH_RATIO = (float)w/480f;
		HEIGHT_RATIO = (float)h/800f;
		
		firstRound = true;
		
		SVG svg1 = SVGParser.getSVGFromResource(getResources(), R.raw.satellite);
		Picture satellitePic = svg1.getPicture();
		
		satelliteContainer = new SatelliteContainer(satellitePic, WIDTH_RATIO, w, h);
		
		players = new Ship[2];
		
		SVG svg2 = SVGParser.getSVGFromResource(getResources(), R.raw.test);
		Picture testPic = svg2.getPicture();
		
		players[0] = new Ship(testPic,WIDTH_RATIO, (float)w/2,(50*WIDTH_RATIO));
		players[1] = new Ship(testPic,WIDTH_RATIO, (float)w/2,h-(50*WIDTH_RATIO));
		players[0].phaseChange(2);
		
		cpuPlayer = new AIPlayer(satelliteContainer.getSatelliteX(), satelliteContainer.getSatelliteY(), w, h);	// set up new AIPlayer
		satelliteContainer.setAi(aiPlaying);
		
		SVG svg3 = SVGParser.getSVGFromResource(getResources(), R.raw.test3);
		probePic = svg3.getPicture();
		
		probe = new Probe(probePic, 100,100, 0, 0,WIDTH_RATIO, HEIGHT_RATIO, false);
		
		SVG svg4 = SVGParser.getSVGFromResource(getResources(), R.raw.phase_ui);
		Picture phasePic = svg4.getPicture();
		
		phaseUI = new UIOverlay(phasePic,WIDTH_RATIO, HEIGHT_RATIO,0*WIDTH_RATIO,0*WIDTH_RATIO);
		
		SVG svg5 = SVGParser.getSVGFromResource(getResources(), R.raw.score_ui);
		Picture scorePic = svg5.getPicture();
		
		scoreUI = new UIOverlay(scorePic, WIDTH_RATIO, HEIGHT_RATIO,w-(scorePic.getWidth())*WIDTH_RATIO,0*WIDTH_RATIO);
	
		 phaseTextOne = getResources().getString(R.string.phase_one_text_one);
		 phaseTextTwo = getResources().getString(R.string.phase_one_text_two);
	}
	
	public void setAvgFps(String avgFps){
		this.avgFps = avgFps;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.setRunning(true);
		thread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{	
		Log.d(TAG, "Surface is being destroyed");
		
		thread.stopThread();
	
		boolean retry = true;
		while(retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
				// try shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedrect){
		Log.d(TAG, "stopped thread");
	}

	
	// USER INPUT
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		
		//TOUCH EVENT
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (hide == true){
				setHide(false);
			}
		}
		
		//if (activePlayer == 0)// ontouch only for the human player
		//{

		if (aiPlaying == true && activePlayer ==1){
		}else{
			switch (gamePhase){				
			case 0:																	// HIDING BOOTY : 
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					addSatelliteMass((int)event.getX(), (int)event.getY());
				}
				break;
			case 1:																	// SENDING PROBE 
				if (event.getAction() == MotionEvent.ACTION_DOWN){						// FIRE THE PROBE
					if (players[activePlayer].handleActionDown((int)event.getX(), (int)event.getY()) == true){
						fireProbe();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE){		// TURN THE SHIP
					players[activePlayer].setAngle((int)event.getX(), (int)event.getY());
				}
				break;
			case 2:																	// STEALING BOOTY 
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					stealMass((int)event.getX(), (int)event.getY());
				}
				break;
			case 3:
				if(event.getY()< 400*WIDTH_RATIO )
				{
					activePlayer = 0;
				} else{
					activePlayer = 1;
				}
				break;
			}
			
		}
		
		return true;
			
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			return true;
		}
		return false;
	}
	
	private void AITurn()
	{
		if (hide == false){
		if (!cpuPlayer.checkWorld())
			cpuPlayer.setPerception(players[0].getScore(), players[1].getScore(), satelliteContainer);
		
		//if (activePlayer ==1 && gamePhase == 1)
			//cpuPlayer.watchProbe(probe.getX(), probe.getY());
		
		switch (cpuPlayer.makeDecision(gamePhase))
		{
			case 0: // DO NOTHING
				break;
			case 1: // PLACE MASS ON SATELLITE
				addSatelliteMass(cpuPlayer.getTargetX(), cpuPlayer.getTargetY());
				cpuPlayer.setPerception(players[0].getScore(), players[1].getScore(), satelliteContainer);
				break;
			case 2:	// TURN & FIRE PROBE
				turnShip(cpuPlayer.getTargetX(), cpuPlayer.getTargetY());
				fireProbe();
				break;
			case 3: // STEAL FROM A SATELLITE
				stealMass(cpuPlayer.getTargetX(), cpuPlayer.getTargetY());
				break;
			case 4:
				break;
		}
		}
		
	}
	
	// UPDATE
	
	public void update()
	{	
		switch (gamePhase){
		case 0:		// HIDING BOOTY
			break;
		case 1:		// SENDING PROBE
			cpuPlayer.setOldProbe(probe.getX(), probe.getY());
			probe.update();
			cpuPlayer.watchProbe(probe.getX(), probe.getY());
			probe.addForce(satelliteContainer.getForce(probe.getX(), probe.getY()));
			if (probe.probeExpired()== true){
				phaseChange(gamePhase);
			}
			break;
		case 2:		// STEALING BOOTY
			break;
		}

		if (aiPlaying == true && activePlayer == 1){
			AITurn();
		}
		
	}

	// DRAWING
	
	protected void render(Canvas canvas)
	{
		// fills the canvas with black
		canvas.drawColor(Color.BLACK);
		// display fps
		if (hide == false){
			displayFps(canvas, avgFps);
			// draw satellites
			satelliteContainer.draw(canvas);
			players[activePlayer].draw(canvas);
			probe.draw(canvas);
			
			phaseUI.draw(canvas);// TEST
			scoreUI.draw(canvas);
			
			//cpuPlayer.suspicion(canvas);
			
			drawProgress(canvas);
		}
		
	}
	
	public void displayFps(Canvas canvas, String fps)
	{
		if(canvas != null && fps != null)
		{
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth()-50, 20 , paint);
		}
	}
	
	protected void drawProgress(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(40.0f);
		canvas.drawText(Integer.toString(players[0].getScore()), 425*WIDTH_RATIO, 45*HEIGHT_RATIO , paint);
		canvas.drawText(Integer.toString(players[1].getScore()), 425*WIDTH_RATIO, 787*HEIGHT_RATIO , paint);
		
		paint.setTextSize(20.0f);
		canvas.drawText(phaseTextOne, 10*WIDTH_RATIO, 28* HEIGHT_RATIO,paint);
		canvas.drawText(phaseTextTwo, 10*WIDTH_RATIO, 48* HEIGHT_RATIO,paint);
	}
	
	// SAVING LOADING AND ENDING GAME
	
	public String getSaveState()
	{
		return xmlHandler.getSaveState(2,gamePhase, activePlayer, players, satelliteContainer);
    }
	
	public void loadSaveState(Bundle save)
	{
		xmlHandler.loadData(save);
		
		loadState(xmlHandler.loadSaveState(players, satelliteContainer));
	}
	
	public void setHide(Boolean hide)
	{
		this.hide = hide;
	}
	
	protected Bundle endGame()
	{
		Bundle endBundle = new Bundle();
		
		if (players[0].getScore() >= 30 || players[1].getScore()>=30){
			endBundle.putBoolean("end", true);
			endBundle.putString("message", "Player "+ activePlayer +" wins");
		}else{
			endBundle.putBoolean("end", false);
		}
		
		return endBundle;
	}

	protected void phaseChange(int gamePhase)
	{
		switch(gamePhase)
		{
			case 0:
				/*if (firstRound)
				{
					if (activePlayer == 1){
						firstRound = false;
					}
					
					phaseChange(2);
					break;
				}else{*/
					this.gamePhase =1;
				//}
				phaseTextOne = getResources().getString(R.string.phase_two_text_one); 
				phaseTextTwo = getResources().getString(R.string.phase_two_text_two);
				players[0].phaseChange(gamePhase);
				players[1].phaseChange(gamePhase);
				break;
			case 1:
				this.gamePhase =2;
				phaseTextOne = getResources().getString(R.string.phase_three_text_one); 
				phaseTextTwo = getResources().getString(R.string.phase_three_text_two);
				players[0].phaseChange(gamePhase);
				players[1].phaseChange(gamePhase);
				break;
			case 2:
				players[activePlayer].turnReset();
				players[activePlayer].addScore(stolen);
				
				this.gamePhase=0;
				
				if (endGame().getBoolean("end") == true)
				{
					setHide(true);
					gamePhase = 3;
					((PlunderActivity)getContext()).queDialogShow(endGame().getString("message"));
					//((Activity)getContext()).showDialog(0, endGame());
				}else{
					if (activePlayer ==0){
						activePlayer =1;
					} else {
						activePlayer =0;
					}
					satelliteContainer.setActivePlayer(activePlayer);
				
					//Bundle bundle = new Bundle();
					//bundle.putString("message", Integer.toString(stolen) + " treasure stolen. Next Player.");
				
					setHide(true);

					((PlunderActivity)getContext()).queDialogShow(Integer.toString(stolen) + " treasure stolen. Next Player.");
				}
				phaseTextOne = getResources().getString(R.string.phase_one_text_one); 
				phaseTextTwo = getResources().getString(R.string.phase_one_text_two);
				players[0].phaseChange(gamePhase);
				players[1].phaseChange(gamePhase);
				break;
		}
	}

	protected void loadState(Bundle bundle)
	{
		this.gamePhase = bundle.getInt("gamePhase");
		this.activePlayer = bundle.getInt("activePlayer");
	}

	// PLAYER ACTIONS
	
	protected void addSatelliteMass(int eventX, int eventY)
	{
		if (satelliteContainer.handleActionDown(gamePhase,eventX, eventY) == true){	// if hit satellite
			if (players[activePlayer].placeMass() == false){	// check if that was the last mass to place
					//gamePhase = 1;
					phaseChange(gamePhase);		// if it was change to phase
				}
			}
	}
	
	protected void fireProbe()
	{
		//if (probe.probeExpired() == true)
		//{
			probe = players[activePlayer].fireProbe(probePic, WIDTH_RATIO, HEIGHT_RATIO);
		//}
	}
	
	protected void turnShip(int eventX, int eventY)
	{
		players[activePlayer].setAngle(eventX, eventY);
	}

	protected void stealMass(int eventX, int eventY)
	{
		if (satelliteContainer.handleActionDown(gamePhase,eventX, eventY)== true)
		{
			stolen = satelliteContainer.getStolen();
			
			if (stolen>=0){
				phaseChange(gamePhase);
			}
		}
	}
	
	public void setAI(Boolean aiPlaying){
		this.aiPlaying = aiPlaying;
	}
	
}
