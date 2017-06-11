package net.marc.plunder.model;

import net.marc.plunder.model.Probe;
import net.marc.plunder.MainThread;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.Log;


public class Ship
{
	private static final String TAG = MainThread.class.getSimpleName();
	private static final String fireText = "FIRE";
	
	private Picture picture;
	private Rect drawRect;
	private float x;
	private float y;
	
	private int width;
	private int height;
	
	private float angle; // in degrees
	
	private Rect fireRect;
	private Rect treasureRect;
	private Rect blankRect;
	private Rect UIRect;
	
	private String UIText;
	
	// instance save values
	private int mass;
	private int score;
	
	
	public Ship(Picture picture,float scale, float x, float y)
	{
		this.picture = picture;
		
		width = picture.getWidth();
		height = picture.getHeight();
		
		width *= 0.8* scale;
		height *= 0.8* scale;
		
		//drawRect = new Rect(-width/2, -height/2 , width/2, height /2);
		drawRect = new Rect(0, 0 , width, height);
		
		this.x = x;
		this.y = y;
		
		if (y > 200){
			angle =0;
		}else{
			angle = 180;
		}
		
		score =0;
		mass = 10;
		
		fireRect = new Rect((int)x+65, (int)y - 40, (int)x+135, (int)y);
		treasureRect = new Rect((int)x-100, (int)y-40, (int)x-50, (int)y);
		blankRect = new Rect(-10, -10, -9, -9);
		
		phaseChange(1);
	}
	
	public void draw(Canvas canvas){
		
		canvas.save();
		canvas.translate(x-width/2, y-height/2);
		canvas.rotate(-angle, width/2, height/2);
		canvas.drawPicture(picture, drawRect);
		canvas.restore();
		
		Paint paint = new Paint();
		paint.setTextSize(30f);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawText(UIText, UIRect.left+5, UIRect.bottom-10, paint);
		
		paint = new Paint();
		paint.setStrokeWidth(3f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(UIRect, paint);
	}
	
	public void update(int eventX, int eventY){
	}
	
	public Boolean handleActionDown(int eventX, int eventY){
		if (eventX >= fireRect.left && eventY <= fireRect.right)//((eventX >=(x- width/2))&& (eventX<=(x+width/2)))
		{
			if(eventY >= fireRect.top && eventY<= fireRect.bottom)//((eventY >=(y-height/2))&&(eventY <=(y+height/2)))
			{
				//Log.d(TAG, "FIRE");
				return true;
			}
		}
		return false;
	}
	
	public double getRotation(){
		return angle;
	}
	
	public void setAngle(int eventX, int eventY){
		double A = x-eventX;
		double O = y-eventY;
		//need 1-angle for <x <y & >x >y
		
		angle = (float)Math.abs((Math.atan(O/A))*(180/Math.PI));
		
		if (eventY < y)
		{
			if (eventX < x)
				angle =0+(90-angle);
			else
				angle =270 + angle;
		}else{
			if (eventX < x)
				angle = 90 + angle;
			else
				angle = 180 + (90-angle);
		}
		
	}
	
	public Probe fireProbe(Picture picture, float x_ratio, float y_ratio)
	{
		// assume x is a unit length
		//float gradient = (float)Math.tan(angle);
		
		float probeAngle = angle;
		int i =0;
		float xV =0;
		float yV=0;
		
		
		while (probeAngle > 90){
			probeAngle -=90;
			i++;
		}
		
		probeAngle = probeAngle * ((float)(2*Math.PI)/360); // turning into rad;
		
		switch(i){			// using trig - x = H * Sin(theta)
		case 0:		// top left quadrant
			xV = -1 *(float)Math.sin(probeAngle); 
			yV = -1 *(float)Math.cos(probeAngle);
			break;
		case 1:		// bottom left quadrant
			xV = -1 * (float)Math.cos(probeAngle); 
			yV =  1 * (float)Math.sin(probeAngle);
			break;
		case 2:		// bottom right quadrant
			xV =  1 *(float)Math.sin(probeAngle); 
			yV =  1 *(float)Math.cos(probeAngle);
			break;
		case 3:		// top right quadrant
			xV =  1 * (float)Math.cos(probeAngle); 
			yV = -1 * (float)Math.sin(probeAngle);
			break;
		}
		
		return new Probe(picture,x, y, xV, yV, x_ratio, y_ratio, true);
	}
	
	public boolean placeMass(){
		if (mass >1)
		{
			mass -= 1;
			UIText = Integer.toString(mass);
			return true;
		}else{
			mass -=1;
			return false;
		}
	}
	
	public void addScore(int points){
		this.score +=points;
	}
	
	public void turnReset(){
		
		if (y>200){
			this.angle = 0;
		}else{
			this.angle =180;
		}
		
		this.mass = 10;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getMass(){
		return mass;
	}
	
	public void loadState(int score, int mass)
	{
		if (y > 200){
			angle =0;
		}else{
			angle = 180;
		}
		
		this.score =score;
		this.mass = mass;
	}
	
	public float getAngle(){
		return angle;
	}
	
	public void phaseChange(int phase){
		switch (phase){
		case 0:
			UIRect = fireRect;
			UIText = fireText;
			break;
		case 1:
			UIRect = blankRect;
			UIText = ".";
			break;
		case 2:
			UIRect = treasureRect;
			UIText = Integer.toString(mass);
			break;
		}
	}
}