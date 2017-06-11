package net.marc.plunder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

public class RoundRushScreen extends MainGamePanel{
	
	private static final String TAG = MainThread.class.getSimpleName();
	private static final int roundCap = 10;
	
	private int round;
	
	public RoundRushScreen(Context context){
		super(context);
		round = 0;
		
		Log.d(TAG, "roundRush");
		
	}
	
	@Override
	protected Bundle endGame()
	{
		Bundle endBundle = new Bundle();
		
		if (this.activePlayer == 1)
		{
			if (round == roundCap)
			{
				endBundle.putBoolean("end", true);
				if (players[0].getScore()> players[1].getScore()){
					endBundle.putString("message", "player 1 wins");
				} else {
					endBundle.putString("message", "player 2 wins");
				}
			}else{
				endBundle.putBoolean("end",false);
			}
			round ++;
		}
		
		return endBundle;
	}
	
	@Override
	protected void drawProgress(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(40.0f);
		canvas.drawText(Integer.toString(players[activePlayer].getScore()), 425*WIDTH_RATIO, 45*HEIGHT_RATIO , paint);
		canvas.drawText(Integer.toString(players[1-activePlayer].getScore()), 425*WIDTH_RATIO, 787*HEIGHT_RATIO , paint);
		
		paint.setTextSize(30.0f);
		canvas.drawText("PHASE " + Integer.toString(gamePhase+1), 10*WIDTH_RATIO, 40* HEIGHT_RATIO,paint);
		
		paint.setARGB(255,0,0,0);
		paint.setTextSize(15.0f);
		canvas.drawText( "TURN: " + round, 7*WIDTH_RATIO, 75*HEIGHT_RATIO,paint);
	}

}
