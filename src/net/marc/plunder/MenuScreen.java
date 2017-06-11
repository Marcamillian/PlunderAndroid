package net.marc.plunder;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import net.marc.plunder.model.MenuButton;
import net.marc.plunder.model.Probe;
import net.marc.plunder.model.Satellite;
import net.marc.plunder.model.Ship;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

//can we ignore this and have a layout instead? YES
public class MenuScreen extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String TAG = MenuThread.class.getSimpleName();
	
	protected float WIDTH_RATIO;
	protected float HEIGHT_RATIO;
	
	protected MenuThread menuThread;
	protected String avgFps;
	
	protected Picture mainRing;
	protected Rect mainRingRect;
	protected Ship ship;
	protected Probe probe;
	
	protected float centreX;
	protected float centreY;
	protected int yShift = -80;
	
	protected String modeName;
	protected int quad;
	protected int mode;
	
	protected MenuButton playButton;
	protected MenuButton tutorialButton;
	protected MenuButton aiButton;
	
	protected boolean aiPlaying;
	
	protected String description;
	
	protected Picture probePic;
	
	public MenuScreen(Context context)
	{
		super(context);
		menuThread = new MenuThread(getHolder(), this);
		
		aiPlaying = true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
	
		WIDTH_RATIO = (float)w/480f;
		HEIGHT_RATIO = (float)h/800f;
		
		centreX = w/2;
		centreY = h/2;
		
		SVG svg1 = SVGParser.getSVGFromResource(getResources(), R.raw.satellite);
		mainRing = svg1.getPicture();
		
		mainRingRect = new Rect((w/2)-(mainRing.getWidth()/2)*10, (h/2)+yShift-(mainRing.getHeight()/2)*10, (w/2)+(mainRing.getWidth()/2)*10, (h/2)+yShift+(mainRing.getHeight()/2)*10);
		
		SVG svg2 = SVGParser.getSVGFromResource(getResources(), R.raw.test);
		Picture shipPic = svg2.getPicture();
		
		ship = new Ship(shipPic, WIDTH_RATIO*2, centreX, centreY+yShift);
		
		SVG svg3 = SVGParser.getSVGFromResource(getResources(), R.raw.test3);
		probePic = svg3.getPicture();
		
		probe = new Probe(probePic, 100,100, 0, 0,WIDTH_RATIO, HEIGHT_RATIO, false);
		
		playButton = new MenuButton("PLAY", (int)centreX, (int)centreY+300 , 100 , 150);
		tutorialButton = new MenuButton("Tutorial", (int)centreX + 150, (int)centreY + 300, 100, 150);
		aiButton = new MenuButton("AI On", (int)centreX - 150, (int)centreY + 300, 100, 150);
		
		
		menuThread.setRunning(true);
		menuThread.start();
		
		Log.d(TAG, "Started MenuThread" );
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		//thread stuff
		menuThread.setRunning(true);
		menuThread.start();
		
		Log.d(TAG, "Started MenuThread" );
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "Surface is being destroyed");
		
		menuThread.stopThread();
	
		boolean retry = true;
		while(retry)
		{
			try
			{
				menuThread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
				// try shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	public void update(){
		//probe.update();
		float angle = ship.getAngle();
		
		if (angle >= 0 && angle <90){
			quad = 4;
			mode = MenuActivity.MODE_POINT_RUSH;
		}else if(angle >= 90 && angle< 180){
			quad = 3;
			mode = MenuActivity.MODE_POINT_LEAD;
		}else if(angle >= 190 && angle< 270){
			quad = 2;
			mode = MenuActivity.MODE_HIDDEN;
		}else if(angle >= 270 && angle< 360){
			quad = 1;
			mode = MenuActivity.MODE_ROUND_RUSH;
		}
		
		switch(mode){
		case MenuActivity.MODE_HIDDEN:
			modeName = getResources().getString(R.string.hidden_tag);
			description = getResources().getString(R.string.hidden_desc);
			break;
		case MenuActivity.MODE_POINT_LEAD:
			modeName = getResources().getString(R.string.point_lead_tag);
			description = getResources().getString(R.string.point_lead_desc);
			break;
		case MenuActivity.MODE_POINT_RUSH:
			modeName = getResources().getString(R.string.point_rush_tag);
			description = getResources().getString(R.string.point_rush_desc);
			break;
		case MenuActivity.MODE_ROUND_RUSH:
			modeName = getResources().getString(R.string.round_rush_tag);
			description = getResources().getString(R.string.round_rush_desc);
			break;
		
		
		}
	}
	
	protected void render(Canvas canvas){
		if (canvas != null){
			
			// clear canvas
			canvas.drawColor(Color.BLACK);
			//draw ship
			ship.draw(canvas);
		
			// draw satellite Ring
			canvas.drawPicture(mainRing, mainRingRect);
			
			// draw Title
			canvas.save();
			canvas.translate(centreX, 70f);
			Paint paint = new Paint();
			paint.setTextSize(50f);
			float titleOffset = paint.measureText("PLUNDER")/2;
			paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			canvas.drawText("PLUNDER", -titleOffset, 0, paint);
			canvas.restore();
			
			//probe.draw(canvas);
			
			//Draw modeName
			canvas.save();
			canvas.translate(centreX, centreY+yShift);
			canvas.rotate((90f*quad)-45f);
			canvas.translate(0, -160);
			paint = new Paint();
			paint.setTextSize(25f);
			if (quad == 2 || quad == 3){
				canvas.rotate(180);
				canvas.translate(0, 15);
			}
			float offset = paint.measureText(modeName)/2;
			paint.setColor(Color.GREEN);
			canvas.drawText(modeName, -offset, 0, paint);
			canvas.restore();
			
			// draw description
			canvas.save();
			canvas.translate(centreX , centreY + 175);
			paint.setTextSize(20f);
			if(mode == MenuActivity.MODE_HIDDEN ){
				paint.setTextSize(15f);
			}
			float descOffset = paint.measureText(description)/2;
			canvas.drawText(description, -descOffset, 0, paint);
			canvas.restore();
			
			//draw buttons
			playButton.draw(canvas);
			tutorialButton.draw(canvas);
			aiButton.draw(canvas);
			
		}
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_MOVE)
			if (event.getY() < 600)
				ship.setAngle((int)event.getX(), (int)event.getY());
		
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			if (playButton.handleActionDown((int)event.getX(), (int)event.getY())){
				((MenuActivity)getContext()).launchMode(mode, aiPlaying);
			} else if (tutorialButton.handleActionDown((int)event.getX(), (int)event.getY())){
				((MenuActivity)getContext()).launchTutorial();
			}else if (aiButton.handleActionDown((int)event.getX(), (int)event.getY())){
				if (aiPlaying == true){
					aiPlaying = false;
					aiButton.setText(getResources().getString(R.string.ai_off));
				}else{
					aiPlaying = true;
					aiButton.setText(getResources().getString(R.string.ai_on));
				}
			}
				
		}
		
		return true;
	}
	
	public void setAvgFps(String avgFps){
		this.avgFps = avgFps;
	}
	
}