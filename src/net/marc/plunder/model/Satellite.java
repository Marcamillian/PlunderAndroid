package net.marc.plunder.model;

import net.marc.plunder.MainThread;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;

public class Satellite{
	
	private static final float G = 1f;
	private static final String TAG = MainThread.class.getSimpleName();
	private static final float FONT_SIZE = 20.0f;
	
	private Picture picture;
	private int x;
	private int y;
	private int width;
	private int height;
	private Rect drawRect;
	
	private float[] appDist = new float[2];
	private int stolen;
	private boolean touched;
	private int activePlayer;
	private boolean aiPlaying;
	
	//instance save values
	private int[] mass;
	
	
	public Satellite (Picture picture,float scale, int x, int y)
	{
		stolen = 0;
		this.picture = picture;
		
		width = picture.getWidth();
		height = picture.getHeight();
		
		width *= 1 * scale;
		height *= 1 * scale;
		
		drawRect = new Rect(-width/2,-height/2,width/2, height/2);
		
		this.x = x;
		this.y = y;

		this.aiPlaying = false;
		
		mass = new int[2];
		mass[0] = 0;
		mass[1]	= 0;
	}
	
	public void update(int phase)
	{
		switch (phase){
		case 0:
			if (touched){
				addMass(1);
			}
			break;
		case 1:
			break;
		case 2:
			if (touched){
				stolen = stealMass();
			}
			break;
		}
		
	}
	
	public void draw(Canvas canvas)
	{
		if(canvas != null)
		{	
			canvas.save();
			canvas.translate(x, y);
			
			canvas.drawPicture(picture, drawRect);
			
			//DRAW MASS
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			paint.setTextSize(FONT_SIZE);
			if(aiPlaying == true && activePlayer ==1){
				canvas.drawText("?", -FONT_SIZE/2, FONT_SIZE*1/3 , paint);
			}else{
				canvas.drawText(Integer.toString(mass[activePlayer]), -FONT_SIZE/2, FONT_SIZE*1/3 , paint);
			}
			
			canvas.restore();
		}
	}
	
	public void drawStolen(Canvas canvas){
		canvas.save();
		canvas.translate(x+5,y+5);
		
		Paint paint = new Paint();
		paint.setColor(Color.MAGENTA);
		canvas.drawCircle(0, 0, 5, paint);
		canvas.restore();
	}
	
	public void reset()
	{
		stolen = 0;
	
		activePlayer = 0;

		mass[0] = 0;
		mass[1]	= 0;
	}
	
	public boolean handleActionDown(int gamePhase,int eventX, int eventY)
	{
		if ((eventX >=(x- (width*1.5)/2))&& (eventX<=(x+(width*1.5)/2)))
		{
			if((eventY >=(y-(height*1.5)/2))&&(eventY <=(y+(height*1.5)/2)))
			{
				setTouched(true);
				update(gamePhase);
				return true;
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}
		
		update(gamePhase);
		return false;
	}
	
	public float[] getForce(float x, float y)
	{
		float[] force = new float[2];
		float xDistance;
		float yDistance;
		float hypDistance;
		float totalForce;
		
		xDistance = x - this.x ;
		yDistance = y - this.y ;
		hypDistance = FloatMath.sqrt(square(xDistance)+square(yDistance)); // works for 25
		
		// force = G * (m1-m2)/d^2
		totalForce = (G*(mass[0]+mass[1]))/hypDistance;
		
		if (xDistance == 0){
			force[0] =0;
		}else{
			force[0] = (totalForce * (-xDistance/hypDistance));	// xForce
		}
		if (yDistance ==0){
			force[1] =0;
		}else{
			force[1] = (totalForce * (-yDistance/hypDistance));	// yForce
		}
		
		//tracking forces
		//appDist[0] = xDistance;
		//appDist[1] = yDistance;
		if (force[0]>0){
			appDist[0] = 30;
		}else if(force[0]<0){
			appDist[0] = -30;
		}else{
			appDist[0] =0;
		}
		
		if (force[1]>0){
			appDist[1] = 30;
		}else if(force[1]<0){
			appDist[1] = -30;
		}else{
			appDist[1] =0;
		}
		
		//Log.d(TAG, "Probe forceX : "+force[0]);
		return force;
	}
	
	public void loadState(int mass1, int mass2)
	{
		mass[0] = mass1;
		mass[1] = mass2;
	}

	public void setTouched(boolean touched){
		this.touched = touched;
	}
	
	// x get-set
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x = x;
	}
	
	// y get-set
	public int getY(){
		return y;
	}
	public void setY(int y){
		this.y = y;
	}
	
	public void addMass(int booty){
		mass[activePlayer] += booty;
	}
	
	private int stealMass(){
		int booty = mass[1-activePlayer];
		mass[1-activePlayer]=0;
		return booty;
	}
	
	private float square(float number){
		return (float)Math.pow(number, 2);
	}

	public int getStolen(){
		return stolen;
	}
	
	public void setStolen(int stolen){
		this.stolen = stolen;
	}
	
	public void setActivePlayer(int activePlayer){
		this.activePlayer = activePlayer;
	}
	
	public int getMass(int sideMass){
		return mass[sideMass];
	}
	
	public void setAi(Boolean aiPlaying){
		this.aiPlaying = aiPlaying;
	}
}