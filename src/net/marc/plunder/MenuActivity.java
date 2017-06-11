package net.marc.plunder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends Activity{

	private static final String TAG = MenuActivity.class.getSimpleName();
	
	public static final int MODE_POINT_RUSH = 1;
	public static final int MODE_POINT_LEAD = 2;
	public static final int MODE_HIDDEN = 3;
	public static final int MODE_ROUND_RUSH = 4;
	
	public MenuScreen menuScreen;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//title off & fullScreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		menuScreen = new MenuScreen(this);
		
		setContentView(menuScreen);
		
		/*setContentView(R.layout.root_menu);
		
		// Tutorial Button
		Button tutorialPlay = (Button)this.findViewById(R.id.tutorial);
		tutorialPlay.setOnClickListener(new View.OnClickListener(){
											@Override
											public void onClick(View v)
											{
												Intent myIntent = new Intent(MenuActivity.this, TutorialActivity.class);
												MenuActivity.this.startActivity(myIntent);
											}
		});
		
		// Point Rush Button
		Button pointRushPlay = (Button)this.findViewById(R.id.point_rush_play);
		pointRushPlay.setOnClickListener(new View.OnClickListener(){
											@Override
											public void onClick(View v)
											{
												Intent myIntent = new Intent(MenuActivity.this, PlunderActivity.class);
												myIntent.putExtra("game_mode", MODE_POINT_RUSH);
												MenuActivity.this.startActivity(myIntent);
											}
		});
		
		// Round Rush Button
		Button roundRushPlay = (Button)this.findViewById(R.id.round_rush_play);
		roundRushPlay.setOnClickListener(new View.OnClickListener(){
											@Override
											public void onClick(View v)
											{
												Intent myIntent = new Intent(MenuActivity.this, PlunderActivity.class);
												myIntent.putExtra("game_mode", MODE_ROUND_RUSH);
												MenuActivity.this.startActivity(myIntent);	
											}
		});
		
		// Point Lead Button
		Button pointLeadPlay = (Button)this.findViewById(R.id.point_lead_play);
		pointLeadPlay.setOnClickListener(new View.OnClickListener(){
											@Override
											public void onClick(View v)
											{
												Intent myIntent = new Intent(MenuActivity.this, PlunderActivity.class);
												myIntent.putExtra("game_mode", MODE_POINT_LEAD);
												MenuActivity.this.startActivity(myIntent);
											}
		});
		
		// Rush Button
		Button hiddenPlay = (Button)this.findViewById(R.id.hidden_play);
		hiddenPlay.setOnClickListener(new View.OnClickListener(){
											@Override
											public void onClick(View v)
											{
												Intent myIntent = new Intent(MenuActivity.this, PlunderActivity.class);
												myIntent.putExtra("game_mode", MODE_HIDDEN);
												MenuActivity.this.startActivity(myIntent);
											}
		});*/
	}
	
	public void launchMode(int gameMode, Boolean aiPlayer){
		Intent myIntent = new Intent(MenuActivity.this, PlunderActivity.class);
		myIntent.putExtra("game_mode", gameMode);
		myIntent.putExtra("ai_player",aiPlayer);

		MenuActivity.this.startActivity(myIntent);
	}
	
	public void launchTutorial(){
		Intent myIntent = new Intent(MenuActivity.this, TutorialActivity.class);
		MenuActivity.this.startActivity(myIntent);
	}
	
}