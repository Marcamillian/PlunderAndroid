package net.marc.plunder.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ElaineAnimated{
	
	private static final String TAG = ElaineAnimated.class.getSimpleName();
	
	private Bitmap bitmap;	// animation sequence
	private Rect sourceRect;	// thie rect to be drawn from ani Bitmap
	private int frameNr;		// number of frames in animation
	private int currentFrame;	//the current frame
	private long frameTicker;	// the time of last frame update
	private int framePeriod; 	// millisecond between each frame
	
	private int spriteWidth;	// the width of sourceRect
	private int spriteHeight; 	// height of the sourceRect
	
	private int x;				// X co-ord
	private int y;				// Y co-ord
	
	
	public ElaineAnimated(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		currentFrame =0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth()/frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0,0,spriteWidth, spriteHeight);
		framePeriod = 1000/fps;
		frameTicker = 01;
	}
	
	public void update(long gameTime)
	{
		if (gameTime > frameTicker + framePeriod)
		{
			frameTicker = gameTime;
			currentFrame++;
			if (currentFrame >= frameNr){
				currentFrame =0;
			}
		}
		
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
	}
	
	public void draw(Canvas canvas)
	{
		Rect destRect = new Rect(getX(), getY(), getX()+spriteWidth, getY()+spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	}
	
	public int getX(){
		return x;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setY(int y){
		this.y = y;
	}
}