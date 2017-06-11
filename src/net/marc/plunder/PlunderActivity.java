package net.marc.plunder;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;

public class PlunderActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String TAG = PlunderActivity.class.getSimpleName();
	
	private static final int MENU1 = Menu.FIRST;
	private static final int MENU2 = Menu.FIRST+1;
	private static final int MENU3 = Menu.FIRST+2;
	private static final int MENU4 = Menu.FIRST+3;
	
	static final int DIALOG_SWAP_PLAYER =0;
	
	public MainGamePanel gameScreen;
	private Bundle saveState; 
	private String dialogMessage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set MainGamePanel as the view
        
        saveState = new Bundle();
        
        Bundle extras = getIntent().getExtras();
        
        switch(extras.getInt("game_mode"))
        {
        	case MenuActivity.MODE_POINT_RUSH:
        		gameScreen = new MainGamePanel(this);
        		Log.d(TAG, "MainGame");
        		break;
        	case MenuActivity.MODE_ROUND_RUSH:
        		gameScreen = new RoundRushScreen(this);
        		Log.d(TAG, "RoundRush Started");
        		break;
        	case MenuActivity.MODE_POINT_LEAD:
        		gameScreen = new PointLeadScreen(this);
        		Log.d(TAG, "Point Lead Started");
        		break;
        	case MenuActivity.MODE_HIDDEN:
        		gameScreen = new HiddenScreen(this);
        		Log.d(TAG, "Hidden Started");
        		break;
        }
        
        setContentView(gameScreen);
        Log.d(TAG, "view added");
        
        Boolean aiPlayer = extras.getBoolean("ai_palyer");
        gameScreen.setAI(extras.getBoolean("ai_player"));
        
    }
    
    @Override
    protected void onDestroy()
    {
    	Log.d(TAG, "Destroying...");
    	super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	super.onSaveInstanceState(outState);
    	outState.putString("save", gameScreen.getSaveState());
    }
    
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	super.onRestoreInstanceState(savedInstanceState);
    	gameScreen.loadSaveState(savedInstanceState);
    	
    }
    
    @Override
    protected void onStop()
    {
    	Log.d(TAG, "Stopping...");
    	super.onStop();
    }
    
    //AlertDialog with button
    @Override
    protected AlertDialog onCreateDialog(int id, Bundle bundle){
    	AlertDialog dialog;
    	switch(id){
    	case DIALOG_SWAP_PLAYER:
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(bundle.getString("message"));
    		builder.setCancelable(true);

    				
    		dialog = builder.create();
    		dialog.setCanceledOnTouchOutside(true);
    		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					gameScreen.setHide(false);
				}
			});
    		
    		break;
    	default:
    		dialog = null;
    	}
    	
    	Log.d(TAG,"tried to launch");
    	return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle){
    	
    	TextView tv = new TextView(this);
    	tv.setText(bundle.getString("message"));
    	dialog.setContentView(tv);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0, MENU1, 0, "Restart");
    	menu.add(0, MENU2, 0, "Quit");
    	menu.add(0, MENU3, 0, "Save");
    	menu.add(0, MENU4, 0 ,"Load");
    	return true;
    }
    
    public void queDialogShow(String quedMessage){
    	
    	dialogMessage = quedMessage;
    	
    	Runnable r = new Runnable(){
    		public void run(){
    			
    			Bundle bundle = new Bundle();
				bundle.putString("message" , dialogMessage);
    			
    			showDialog(0,bundle);
    		}
    	};
    	
    	runOnUiThread(r);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId())
    	{
    	case MENU1:
    		resetGame();
    		return true;
    	case MENU2:
    		finish();
    		return true;
    	case MENU3:
    		saveState.putString("save",gameScreen.getSaveState());
    		return true;
    	case MENU4:
    		gameScreen.loadSaveState(saveState);
    		return true;
    	}
    	return false;
    }
    
    public void resetGame(){
    	 setContentView(new MainGamePanel(this));
    }
}