package  net.marc.plunder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class TutorialActivity extends Activity {
	
	private static final int MENU1 = Menu.FIRST;
	
	private static final int[] subSections = {4,1,5,2,5};
	static final int DIALOG_SWAP_PLAYER =0;

	Boolean viewActive = false;
	int tutorialSection =0;
	int tutSubSection = 0;
	
	Button[] sectionButtons = new Button[5];
	/*Button basicButton;
	Button plantButton;
	Button otherButton;
	Button stealButton;
	Button tipsButton;*/
	
	TutorialScreen tutScreen;
	TextView tutText;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.tutorial_screen);
        
        //TUTORIAL TEXT
         tutText = (TextView)this.findViewById(R.id.tutorial_text);
         
        //TUTORIAL SECTION BUTTONS
         
        Button basicButton = (Button)this.findViewById(R.id.basics);
        basicButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										viewActive = false;
        										tutorialSection= 0;
        										tutScreen.screenReset();
        										tutSubSection=0;
        										sectionFlipper();
        										buttonFlipper();
        									}
        								});
        
        Button plantButton = (Button)this.findViewById(R.id.plant);
        plantButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										viewActive = true;
        										tutorialSection= 1;
        										tutScreen.screenReset();
        										tutSubSection=0;
        										sectionFlipper();
        										buttonFlipper();
        									}
        								});
        
        Button otherButton = (Button)this.findViewById(R.id.other);
        otherButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										viewActive = false;
        										tutorialSection= 2;
        										tutScreen.screenReset();
        										tutSubSection=0;
        										sectionFlipper();
        										buttonFlipper();
        									}
        								});
        
        Button stealButton = (Button)this.findViewById(R.id.stealing);
        stealButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										viewActive = true;
        										tutorialSection= 3;
        										tutScreen.screenReset();
        										tutSubSection=0;
        										sectionFlipper();
        										buttonFlipper();
        									}
        								});
        
        Button tipsButton = (Button)this.findViewById(R.id.tips);
        tipsButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										viewActive = false;
        										tutorialSection= 4;
        										tutScreen.screenReset();
        										tutSubSection=0;
        										sectionFlipper();
        										buttonFlipper();
        									}
        								});
        
        sectionButtons[0] = basicButton;
        sectionButtons[1] = plantButton;
        sectionButtons[2] = otherButton;
        sectionButtons[3] = stealButton;
        sectionButtons[4] = tipsButton;
        
        basicButton.setPressed(true);
        
        // SECTION NAVIGATION BUTTONS
        
        
        Button nextButton = (Button)this.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										if(tutSubSection < subSections[tutorialSection])
        										{
        											tutSubSection ++;
        											sectionFlipper();
        										} else {
        											if (tutorialSection <4){
        												viewActive = false;
        												tutorialSection++;
        												tutScreen.screenReset();
        												tutSubSection=0;
        												sectionFlipper();
        												buttonFlipper();
        											}
        										}
        									}
        								});
        
        Button previousButton = (Button)this.findViewById(R.id.back);
        previousButton.setOnClickListener(new View.OnClickListener()
        								{
        									@Override
        									public void onClick(View v)
        									{
        										if(tutSubSection > 0)
        										{
        											tutSubSection --;
        											sectionFlipper();
        										}else {
        											if (tutorialSection >0){
        												viewActive = false;
        												tutorialSection --;
        												tutScreen.screenReset();
        												tutSubSection= subSections[tutorialSection];
        												Log.d("tutorial","" +subSections[tutorialSection]);
        												sectionFlipper();
        												buttonFlipper();
        											}
        										}
        									}
        								});
        
        // Grab textView
        tutScreen = (TutorialScreen)this.findViewById(R.id.something);
        tutScreen.setTutorialState(5);
    }
    
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0, MENU1, 0, "Back to Menu");
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId())
    	{
    	case MENU1:
    		finish();
    		return true;
    	}
    	return false;
    }
    
    @Override
    protected AlertDialog onCreateDialog(int id, Bundle bundle){
    	AlertDialog dialog;
    	switch(id){
    	case DIALOG_SWAP_PLAYER:
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(bundle.getString("message"));
    		builder.setCancelable(true);
    		/*builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
    				public void onClick(DialogInterface dialog, int id){
    					dialog.cancel();
    				}
    			}
    		);*/
    				
    		dialog = builder.create();
    		dialog.setCanceledOnTouchOutside(true);
    		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					tutScreen.setHide(false);
				}
			});
    		
    		break;
    	default:
    		dialog = null;
    	}
    	
    	//Log.d(TAG,"tried to launch");
    	return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle){
    	
    	TextView tv = new TextView(this);
    	tv.setText(bundle.getString("message"));
    	dialog.setContentView(tv);
    }
    
    private void sectionFlipper()
    { 	
    	
    	switch(tutorialSection)
    	{
    	case 0:
    		
    		viewActive = false;
    		tutScreen.screenReset();
    		
    		switch(tutSubSection)
    		{
    		case 0:
    			tutText.setText(R.string.one_one);
    			// show ship alone
    			tutScreen.setTutorialState(5);
    			break;
    		case 1:
    			tutText.setText(R.string.one_two);
    			//show treasure counter
    			tutScreen.setTutorialState(5);
    			break;
    		case 2:
    			tutText.setText(R.string.one_three);
    			// show satellites
    			tutScreen.setTutorialState(6);
    			break;
    		case 3:
    			tutText.setText(R.string.one_four);
    			tutScreen.setTutorialState(1);
    			// show mass going on
    			break;
    		case 4:
    			tutText.setText(R.string.one_five);
    			// show probe getting deflected
    			tutScreen.setGamePhase(1);
    			tutScreen.setTutorialState(1);
    			tutScreen.setTutorialState(2);
    			break;
    		}
    		break;
    	case 1:

    		viewActive =true;
    		
    		switch(tutSubSection)
    		{
    		case 0:
    			tutText.setText(R.string.two_one);
    			tutScreen.setGamePhase(0);
    			// gamePhase 1
    			break;
    		case 1:
    			tutText.setText(R.string.two_two);
    			tutScreen.setGamePhase(1);
    			//gamePhase 2 (reset state?)
    			break;
    		}
    		break;
    	case 2:

    		viewActive = false;
    		tutScreen.screenReset();
    		
    		switch(tutSubSection)
    		{
    		case 0:
    			tutText.setText(R.string.three_one);
    			// show other ship
    			break;
    		case 1:
    			tutText.setText(R.string.three_two);
    			tutScreen.setTutorialState(3);
    			// show other placing stuff
    			break;
    		case 2:
    			tutText.setText(R.string.three_three);
    			tutScreen.setTutorialState(3);
    			tutScreen.setTutorialState(1);
    			// nothing - more treasure?
    			break;
    		case 3:
    			tutText.setText(R.string.three_four);
    			// switch to showing YOUR stash
    			tutScreen.setTutorialState(3);
    			tutScreen.setTutorialState(1);
    			tutScreen.setTutorialState(4);
    			break;
    		case 4:
    			tutText.setText(R.string.three_five);
    			// fire projectile
    			tutScreen.setTutorialState(3);
    			tutScreen.setTutorialState(1);
    			tutScreen.setTutorialState(4);
    			tutScreen.setTutorialState(2);
    			break;
    		case 5:
    			tutText.setText(R.string.three_six);
    			// show tap to steal (animation?)
    			//tutScreen.setTutorialState(3);
    			break;
    		}
    		break;
    	case 3:
    		
    		viewActive = true;
    		
    		switch(tutSubSection)
    		{
    		case 0:
    			tutText.setText(R.string.four_one);
    			// hide treasure
    			tutScreen.screenReset();
    			tutScreen.setTutorialState(3);
    			tutScreen.setTutorialState(7);
    			tutScreen.setTutorialState(4);
    			break;
    		case 1:
    			tutText.setText(R.string.four_two);
    			// firing probes
    			tutScreen.setGamePhase(1);
    			break;
    		case 2:
    			tutText.setText(R.string.four_three);
    			//steal them
    			tutScreen.setGamePhase(2);
    			break;
    		}
    		break;
    	case 4:
    		
    		viewActive = false;
    		tutScreen.screenReset();
    		
    		switch(tutSubSection)
    		{
    		case 0:
    			tutText.setText(R.string.five_one);
    			break;
    		case 1:
    			tutText.setText(R.string.five_two);
    			break;
    		case 2:
    			tutText.setText(R.string.five_three);
    			break;
    		case 3:
    			tutText.setText(R.string.five_four);
    			// show gravity deflection away from high value
    			break;
    		case 4:
    			tutText.setText(R.string.five_five);
    			// show complex path using gravity
    			break;
    		case 5:
    			tutText.setText(R.string.five_six);
    			break;
    		}
    		break;
    	}
    }

    private void buttonFlipper(){
    	for (Button b :sectionButtons){
    		b.setPressed(false);
    	}
    	sectionButtons[tutorialSection].setPressed(true);
    }
    
    public Boolean viewInteract()
    {
    	return viewActive;
    }
}