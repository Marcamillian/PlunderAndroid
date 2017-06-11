package net.marc.plunder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class HiddenScreen extends MainGamePanel{
	
	private static final String TAG = MainThread.class.getSimpleName();
	
	public HiddenScreen(Context context){
		super(context);
		Log.d(TAG, "roundRush");
	}
	
	@Override
	protected Bundle endGame()
	{
		Bundle endBundle = new Bundle();
		//UPDATE FOR NEW RULESET
		
		if(satelliteContainer.satMassCount(activePlayer, 10) >= 3)
		{
			endBundle.putBoolean("end", true);
			if( players[0].getScore() > players[1].getScore())
			{
				endBundle.putString("message", "Player 1 wins");
			} else if( players[1].getScore() > players[0].getScore()){
				endBundle.putString("message", "Player 1 wins");
			} else{
				endBundle.putString("message", "Game drawn!");
			}	
		} else {
			endBundle.putBoolean("end", false);
		}
		
		return endBundle;
	}

}