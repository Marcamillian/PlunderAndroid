package net.marc.plunder.model;

import java.util.ArrayList;
import java.util.List;

import net.marc.plunder.model.Probe;
import net.marc.plunder.MainThread;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.Log;


public class AIPlayer 
{
	private static final String TAG = MainThread.class.getSimpleName();
	
	private int waitTime = 0;
	private int waitTimeMax = 50;
	
	private int targetX;
	private int targetY;
	private int xSpacing;
	private int ySpacing;
	
	private boolean worldChecked = false;
	private boolean hasFired = false;
	
	private boolean cocky;						//boolean flag
	private int cockyPointLeadLimit = 5;		//> point limit = still cocky
	
	//world perceptionVariables
	private int[] satelliteField;
	private int pointLead;
	private float[] probePosition = new float[2]; // 0 - xPos // 1- yPos
	private float[] lastProbePos = new float[2]; // 0 - xPos // 1 - yPos
	private float[] lastProbeVector = new float[3];
	
	//suspicious nodes
	private List<float[]> suspectPoints = new ArrayList<float[]>();
	private int[] suspicionMap = new int[16];
	
	public AIPlayer(int x, int y, int width, int height)
	{
		targetX = x;
		targetY = y;
		
		xSpacing = width/5;
		ySpacing = height/5;
		// set grid spacings so can know what connects to what??
		satelliteField = new int[16];
		pointLead = 0;
		
		if (Math.random() > 0.5)
			cocky = true;
		else
			cocky = false;
		
		probePosition[0] = 0;
		probePosition[1] = 0;
		
		lastProbePos[0] = 0;
		lastProbePos[1] = 0;
		
		lastProbeVector[0] = 0;
		lastProbeVector[1] = 0;
		lastProbeVector[2] = 0;
		
	}
	
	public void setPerception(int theirScore, int myScore, SatelliteContainer satelliteContainer){
		
		for (int i =0; i<16; i++)
		{
			satelliteField[i] = satelliteContainer.getSatelliteMass(i);
		}
		
		pointLead = myScore - theirScore;
		
		if (pointLead > cockyPointLeadLimit)
		{
			cocky = true;
		} else if (pointLead < -cockyPointLeadLimit){
			cocky = false;
		}
		
		worldChecked = true;
	}

	public void watchProbe(float posX, float posY)
	{
		probePosition[0] = posX;
		probePosition[1] = posY;
		//Log.d("cpuPlayer", probePosition[0]+":"+ lastProbePos[0]);

	}
	
	public void setOldProbe(float posX, float posY){
		lastProbePos[0] = posX;
		lastProbePos[1] = posY;
	}
	
	public int makeDecision(int gamePhase)
	{
		/* decisions
		 * 0 - do nothing
		 * 1 - steal
		 * 2 - turn and fire
		 * 3 - steal
		 * 4 - waatch
		 */
		
		//if (waitTime < waitTimeMax){
		//	waitTime++;
		//	return 0;	
		//}
		
		switch(gamePhase){
		case 0:
			waitTime = 0;
			placeDecision();
			return 1;
		case 1:
			if (!hasFired){
				waitTime = 0;
				hasFired = true;
				targetDecision();
			}else{
				watchFlight();
				return 4;
			}
			return 2;
		case 2:
			hasFired = false;
			waitTime = 0;
			worldChecked = false;
			
			int target = stealDecision();//(int)(Math.random()*15);//
			satelliteToCoOrd(target);
			
			return 3;
		}
		
		return 0;
	}
	
	private void placeDecision(){
		/* 
		 * check how cocky you are
		 * 
		 * see how they are distributed
		 */
		
		// starts with a random satellite
		int target = (int)(Math.random()*15);
		
		//if anything beats it then switch to it // FINDS HIGHEST
		
		if (cocky)
		{
			for (int i=0 ; i<16 ; i++)
			{
				if (satelliteField[i] > satelliteField[target] || satelliteField[target] > 10)
				{
					target = i;
				}
			}
		} else {
			
			int targetDifference = Math.abs(satelliteField[target]-satelliteField[target+1]);
			
			for (int i=0; i< (16-1); i++)
			{
				int difference = Math.abs(satelliteField[i]-satelliteField[i+1]);
				if (difference > 5 && difference > targetDifference)
				{
					if (satelliteField[i] > satelliteField[i+1]){
						target = i+1;
					} else if (satelliteField[i] < satelliteField[i+1]){
						target = i;
					} else if (satelliteField[i] == satelliteField[i+1]){
						target = i;
					}
					
					if (target == 15)
						targetDifference = Math.abs(satelliteField[target] - satelliteField[target-1]);
					else
						targetDifference = Math.abs(satelliteField[target]-satelliteField[target+1]);
				}
			}
		}
		
		
		//
		// set the target to the highest
		satelliteToCoOrd(target);
	}
	
	private void targetDecision()
	{
		int target = (int)(Math.random()*15);
		satelliteToCoOrd(target);
		/* areas that either we dont know about or need to investigate more
		 * DOESNT DEPEND ON THE SATELLITE POSITIONS
		 */
	}
	
	private void watchFlight()
	{		
		float deltaX = probePosition[0] - lastProbePos[0];
		float deltaY = probePosition[1] - lastProbePos[1];
		
		float[] unitVector = new float[2];
		unitVector[0] = deltaX / Math.abs(deltaX);
		unitVector[1] = deltaY / Math.abs(deltaX);
		
		if (unitVector[1] == lastProbeVector[1] &&  unitVector[1] == lastProbeVector[1])
		{

		}else{
			// set up the recorded value
			float[] point = new float[8];
			point[0] = probePosition[0];
			point[1] = probePosition[1];

			//vector it moved this time
			point[2] = deltaX;
			point[3] = deltaY;
			
			// predicted point
			point[4] = lastProbePos[0] + (lastProbeVector[0]*lastProbeVector[2]); // unit vector * magnitude
			point[5] = lastProbePos[1] + (lastProbeVector[1]*lastProbeVector[2]);
			
			// vector difference - delta (nowPosition - expected Position)
			point[6] = probePosition[0] - point[4];
			point[7] = probePosition[1] - point[5];
			
			if (point[7] < 800){
				suspectPoints.add(point);
			}
		}
		
		lastProbeVector[0] = unitVector[0];
		lastProbeVector[1] = unitVector[1];
		lastProbeVector[2] = Math.abs(deltaX);
		//lastProbePos = probePosition;
		
	}
	
	private int stealDecision(){
		
		for(float[] point : suspectPoints){
			// FIND WHAT SQUARE WE ARE IN
			int row = 0;
			int col = 0;
			
			//point X - to column
			while ( point[0] > (col+1)*xSpacing){
				col++;
			}
			
			//point Y - to row
			while ( point[1] > (row+1)*ySpacing){
				row++;
			}
			
			// nodes surrounding the segment
			int bottomRight = (row*4)+(col);
			int bottomLeft = (row*4)+(col-1);
			int topRight = ((row-1)*4)+ col;
			int topLeft = ((row-1)*4)+ (col-1);
			
			// FIND WHICH DIRECTION IT WAS POINTING & add to suspects list
			if (point[6]<0){ //either RIGHT hand edge or LEFT edge
				
				if( bottomLeft >= row*4 && bottomLeft<16)
					suspicionMap[bottomLeft]++;
				if( topLeft >= 0 && topLeft >= (row-1)*4) 
					suspicionMap[topLeft]++;
				
			}else if(point[6]>0){
				if (bottomRight < (row+1)*4 && bottomRight <16)
					suspicionMap[bottomRight]++;
				if (topRight > 0 && topRight< (row*4))
					suspicionMap[topRight]++;
			}
			
			if (point[7]>0) // bottom row
			{
				if (bottomRight < (row+1)*4 && bottomRight<16)
					suspicionMap[bottomRight]++;
				if (bottomLeft >= row*4 && bottomLeft <16)
					suspicionMap[bottomLeft]++;
			}else{	 // or top row 
				if (topRight < (row*4) && topRight >0)
					suspicionMap[topRight]++;
				if( topLeft >= 0 && topLeft >= (row-1)*4) 
					suspicionMap[topLeft]++;
			}
		}
		
		suspectPoints.clear();
		
		// delete from map the ones you know ate problems
		for (int i=0; i<16; i++){
			if (satelliteField[i]>0){
				suspicionMap[i]/=2;
			}
		}
		
		
		// pick the biggest in the map
		int targetSatellite = (int)(Math.random()*15);
		
		for (int i=0; i<16 ; i++){
			if (suspicionMap[i] > suspicionMap[targetSatellite]){
				targetSatellite = i; 
			}
		}
		suspicionMap[targetSatellite]=0;
		return targetSatellite;
		
	}
	
	public int getTargetX(){
		return targetX;
	}
	
	public int getTargetY(){
		return targetY;
	}
	
	private void satelliteToCoOrd(int satelliteNumber)
	{
		int xSpaces = satelliteNumber % 4;
		targetX = (1+xSpaces) * xSpacing;
		
		int ySpaces = (satelliteNumber - xSpaces)/4;  
		targetY = (1+ySpaces)*ySpacing;
	}
		
	public boolean checkWorld(){
		
		if (worldChecked)
			return true;
		else
			return false;
		
	}
	
	public void suspicion(Canvas canvas){
		Paint paint = new Paint();
		
		for (float[] point : suspectPoints ){  // FLIP THROUGH AND DRAW SUSPECT POINTS
			canvas.drawCircle(point[0], point[1], 5, paint);
			paint.setColor(Color.YELLOW);
			canvas.drawLine(point[0], point[1], point[0]+(10*point[6]), point[1]+(10*point[7]), paint);
		}
	}
	
}

