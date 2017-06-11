package net.marc.plunder.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import net.marc.plunder.PlunderActivity;

public class MenuButton{
	
	private static final String TAG = PlunderActivity.class.getSimpleName();
	
	private int x;
	private int y;
	private int height;
	private int width;
	
	private String text;
	
	public MenuButton(String text, int x, int y, int width, int height)
	{
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void update(){
		
	}
	
	public void draw(Canvas canvas){
		
		canvas.save();
		canvas.translate(x,y);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		paint.setColor(Color.WHITE);
		
		canvas.drawRect(-width/2, -height/2, width/2, height/2, paint);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(20);
		canvas.drawText(text, -paint.measureText(text)/2, 0, paint);
		canvas.restore();
		
		
	}
	
	public boolean handleActionDown(int eventX, int eventY){
		
		if (eventX > x-width/2 && eventX < x+width/2){
			if (eventY > y-height/2 && eventY < y+height/2)
				return true;
		}
		return false;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
}