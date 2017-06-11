package net.marc.plunder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class PointLeadScreen extends MainGamePanel{
	
	private static final String TAG = MainThread.class.getSimpleName();
	
	public PointLeadScreen(Context context){
		super(context);
		Log.d(TAG, "roundRush");
	}
	
	@Override
	protected Bundle endGame()
	{
		Bundle endBundle = new Bundle();
		
		if (Math.abs(players[0].getScore() - players[1].getScore()) > 10)
		{
			endBundle.putBoolean("end", true);
			
			if (players[0].getScore() > players[1].getScore())
			{
				endBundle.putString("message", "player 1 wins");
			} else if (players[0].getScore() < players[1].getScore()){
				endBundle.putString("message", "player 2 wins");
			}
			
		}else{
			endBundle.putBoolean("end", false);
		}
		
		return endBundle;
	}

}
