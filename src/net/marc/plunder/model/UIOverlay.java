package net.marc.plunder.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;

public class UIOverlay{

	private Picture picture;
	
	private float x;
	private float y;
	private int width;
	private int height;
	
	private Rect drawRect;
	
	public UIOverlay(Picture picture,float x_scale, float y_scale, float x, float y)
	{
		
		this.picture = picture;
		
		width = picture.getWidth();
		height = picture.getHeight();
		
		width *= 1 * x_scale;
		height *= 1 * y_scale;
		
		drawRect = new Rect(0,0, width, height);
		
		this.x = x;
		this.y = y;
	}
	
	public void draw(Canvas canvas)
	{
		canvas.save();
		canvas.translate(x, y);
		canvas.drawPicture(picture, drawRect);
		canvas.restore();
	}
}
	
	