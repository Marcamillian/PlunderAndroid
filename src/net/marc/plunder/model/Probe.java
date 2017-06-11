package net.marc.plunder.model;

import net.marc.plunder.MainThread;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.Log;


public class Probe
{
	private static final String TAG = MainThread.class.getSimpleName();
	
	private static final int MAX_FLY_TIME = 500;
	private static final int speed = 5; 
	
	//private Bitmap bitmap;
	private Picture picture;
	private float x;
	private float y;
	private int width;
	private int height;
	private Rect drawRect;
	
	
	private float velocityX;
	private float velocityY;
	private int flyTime;
	
	private boolean active;
	private boolean expired;
	private float xBound;
	private float yBound;
	
	private float addedForceX;
	private float addedForceY;
	
	public Probe(Picture picture, float x, float y, float xV, float yV, float x_ratio, float y_ratio, boolean active)
	{
		//this.bitmap = bitmap;
		
		this.picture = picture;
		
		width = picture.getWidth();
		height = picture.getHeight();
		
		width *= 1.5;
		height *= 1.5;
		
		drawRect = new Rect(0, 0 , width, height);
		
		this.x = x;
		this.y = y;
		
		velocityX = speed * xV;
		velocityY = speed * yV;
		
		this.active = active;
		this.expired = false;
		
		xBound = 480*x_ratio;
		yBound = 800*y_ratio;
	}
	
	public void update()
	{
		if ( x>0 && x<xBound && y >0 && y <yBound && flyTime < MAX_FLY_TIME){
			x += (int)velocityX;
			y += (int)velocityY;
			flyTime ++;
		} else{
			expired = true;
		}
	}
	
	public void draw(Canvas canvas)
	{
		if (active)
		{
			//canvas.drawBitmap(bitmap, x-(bitmap.getWidth()/2), y-(bitmap.getHeight()/2), null);
			canvas.save();
			canvas.translate(x-width*1/2, y-height*1/2);
			canvas.drawPicture(picture, drawRect);
			canvas.restore();
		}
	}
	
	public void addForce(float[] force)
	{
		velocityX += force[0];
		velocityY += force[1];
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public boolean probeExpired(){
		if (active== true && expired == true)
		{
			makeDormant();
			return true;
		}
		return false;
	}
	
	public void makeDormant(){
		this.x = 0;
		this.y =0;
		this.velocityX=0;
		this.velocityY =0;
		active = false;
		flyTime = 0;
	}
	
}