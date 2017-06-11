package net.marc.plunder;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


//can we ignore this and have a layout instead? YES
public class TutorialScreen extends MainGamePanel
{
	private int tutorialStage = 0;
	private static final String TAG = "TutorialActivity";
	private Boolean showShip = true;
	private Boolean showSat = true;
	
	
	public TutorialScreen(Context context)
	{
		super(context);
		aiPlaying = true;
	}
	
	public TutorialScreen(Context context, AttributeSet attrs) {	 
		super( context, attrs );
		aiPlaying = true;
	}
		 
	public TutorialScreen(Context context, AttributeSet attrs, int defStyle) { 
		super( context, attrs, defStyle );
		aiPlaying = true;
	}
	
	
	@Override
	public void update()
	{
		switch (gamePhase){
		case 0:		// HIDING BOOTY
			break;
		case 1:		// SENDING PROBE
			probe.update();
			probe.addForce(satelliteContainer.getForce(probe.getX(), probe.getY()));
			if (probe.probeExpired()== true){
				//Log.d(TAG, "probe expired");
			}
			break;
		case 2:		// STEALING BOOTY
			break;
		}
	}
	
	protected void render(Canvas canvas)
	{
		// fills the canvas with black
		canvas.drawColor(Color.BLACK);
		// display fps
		if (hide == false){
			//displayFps(canvas, avgFps);
			// draw satellites
			if (showSat){
				satelliteContainer.draw(canvas);
			}
			if (showShip){
				players[activePlayer].draw(canvas);
			}
			probe.draw(canvas);
			
			drawProgress(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		if (((TutorialActivity)getContext()).viewInteract())
		{
		/*if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (event.getY() > getHeight() -50)		// ALWAYS QUIT IF IN BOTTOM 50 px
			{
				thread.setRunning(false);
				((Activity)getContext()).finish();
			}
			else
			{
				//Log.d(TAG, "Coords: x=" + event.getX() + ", y="+ event.getY());
			}
		}*/
		
		//TOUCH EVENT
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (hide == true){
				hide = false;
			}
		}
		
		switch (gamePhase){				
		case 0:		// HIDING BOOTY : ship(mass) satellite(mass)
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				if (satelliteContainer.handleActionDown(gamePhase,(int)event.getX(), (int)event.getY()) == true){	// if hit satellite
					if (players[activePlayer].placeMass() == false){
						//Log.d(TAG,xmlHandler.getSaveState(2, gamePhase, activePlayer, players, satelliteContainer));
					}
				}
			}
			break;
		case 1:		// SENDING PROBE : ship(rotation, fire) - probe(gravity)
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				if (players[activePlayer].handleActionDown((int)event.getX(), (int)event.getY()) == true){
					probe = players[activePlayer].fireProbe(probePic,WIDTH_RATIO, HEIGHT_RATIO);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE){
				players[activePlayer].setAngle((int)event.getX(), (int)event.getY());
			}
			break;
		case 2:		// STEALING BOOTY : ship(mass) satellite (mass)
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
					if (satelliteContainer.handleActionDown(gamePhase,(int)event.getX(), (int)event.getY())== true)
						{
							int amountStolen = satelliteContainer.getStolen();
						
							if (amountStolen>=0)
							{
									Bundle bundle = new Bundle();
									bundle.putString("message", Integer.toString(amountStolen) + " treasure stolen. Next Player.");
							
									hide = true;
							
									((Activity)getContext()).showDialog(0, bundle);
							}
						}
				}
				break;
			}
		}
		return true;
	}
	
	public void setGamePhase(int phase){
		this.gamePhase = phase;
	}
	
	public void setTutorialState(int part)// for non-interactive parts
	{
		switch(part)
		{
		case 1:	// adding mass
			satelliteContainer.addMass(5, 15);
			break;
		case 2: // fire probe
			probe = players[activePlayer].fireProbe(probePic, WIDTH_RATIO, HEIGHT_RATIO);
			break;
		case 3: // change players - 1
			activePlayer =1;
			satelliteContainer.setActivePlayer(activePlayer);
			break;
		case 4:	// change players 0
			activePlayer = 0;
			satelliteContainer.setActivePlayer(activePlayer);
			break;
		case 5:	// show ship
			showSat = false;
			break;
		case 6:	//show sat
			showShip = false;
			break;
		case 7:
			satelliteContainer.addMass(6, 15);
			satelliteContainer.addMass(1, 10);
			satelliteContainer.addMass(15, 5);
			satelliteContainer.addMass(8, 15);
			break;
		}
		
	}
	
	public void setHide(Boolean hide){
		this.hide = hide;
	}
	
	public void screenReset(){
		satelliteContainer.reset();
		probe.makeDormant();
		hide = false;
		showShip = true;
		showSat = true;
		players[0].turnReset();
		players[1].turnReset();
	}
	
}