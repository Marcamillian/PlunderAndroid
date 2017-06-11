package net.marc.plunder.model;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import net.marc.plunder.MainThread;
import net.marc.plunder.model.Satellite;


public class SatelliteContainer{
	
	private static final String TAG = MainThread.class.getSimpleName();
	
	private int stolen;
	private Satellite[] satellites;
	private int xSpace;
	private int ySpace;
	private int lastStolen = -1;
	
	public SatelliteContainer(Picture picture, float scale, int width, int height)
	{
		satellites = new Satellite[16];
		
		xSpace= width/5;
		ySpace= height/5;
		
		stolen = 0;
		
		for (int i = 0; i < 4 ; i++)
		{
			int yPos = ySpace*(1+ i); 
			
			for (int j=0; j < 4; j++)
			{
				int xPos = xSpace*(1+j);
				satellites[i*4+j]= new Satellite(picture, scale, xPos, yPos);
			}
		}
	}
	
	public void draw(Canvas canvas)
	{
		//draw each satellite
		for (int i =0; i<16; i++)
		{
			satellites[i].draw(canvas);
		}
		
		if (lastStolen >= 0){
			satellites[lastStolen].drawStolen(canvas);
		}
	}
	
	public boolean handleActionDown(int gamePhase,int eventX, int eventY)
	{
		// ADDING TO SATELLITES
		int row = 0;
		int col = 0;
		
		boolean responded = false;
		
		//event Y - to row
		while ( eventY > (row+1.5)*ySpace){
			row++;
		}
		//event X - to column
		while ( eventX > (col+1.5)*xSpace){
			col++;
		}
		
		// send to satellite
		if ( row < 4 && col < 4)
		{
			//satellites[(row*4)+col].addMass(1);
			responded = satellites[(row*4)+col].handleActionDown(gamePhase, eventX, eventY);
		
			if (gamePhase ==2){	
				
				lastStolen = (row*4)+col;
				
				if (satellites[(row*4)+col].getStolen()> 0)
				{
					stolen = satellites[(row*4)+col].getStolen();
				}
			}
		}
		
		return responded;
	}
	
	
	public float[] getForce(float x, float y) // return the force
	{
		float[] force = new float[2];
		int row = 0;
		int col = 0;
		int[] forceSats = new int[4];
		
		// find row and column
		while ( y > (row+1)*ySpace){	//event Y - to row
			row++;
		}
		
		while ( x > (col+1)*xSpace){	//event X - to column
			col++;
		}

		
		// turn grid ref into node ref - 
		if (row >= 0 && col >= 0 && row <=4 && col <=4)
		{
			forceSats[0] = (4*(row-1))+(col-1);
			forceSats[1] = (4*(row-1))+(col);
			forceSats[2] = (4*(row))+(col-1);
			forceSats[3] = (4*(row))+(col);
		} 
		
		if (row == 0){ // if top row
			forceSats[0] = 50;
			forceSats[1] = 50;
			
			if (col == 0){
				forceSats[2] = 50;
			}
			if (col == 4){
				forceSats[3] = 50;
			}
		} else if (row == 4){ // if bottom row
			
			forceSats[2] = 50;
			forceSats[3] = 50;
			
			if (col == 0){
				forceSats[0] = 50;
			}
			if (col == 4){
				forceSats[1] = 50;
			}
		} else if (col == 0){ // if left col
			
			forceSats[0] = 50;
			forceSats[2] = 50;
			
			if (row == 0){
				forceSats[1] = 50;
			}
			if (row == 4){
				forceSats[3] = 50;
			}
		} else if (col == 4){ // if right col
			
			forceSats[1] = 50;
			forceSats[3] = 50;
			
			if (row == 0){
				forceSats[0] = 50;
			}
			if (row == 4){
				forceSats[2] = 50;
			}
		}
		
		for (int i=0; i<4; i++)
		{
			if (forceSats[i] < 16)
			{
				float[] singleForce;
				
				singleForce = satellites[forceSats[i]].getForce(x,y);
				//satellites[forceSats[i]].hightlight();
				
				force[0] += singleForce[0];
				force[1] += singleForce[1];
			}
		}
		return force;
	}
	
	public int getStolen(){
		int roundStolen = stolen;
		stolen =0;
		return roundStolen;
	}
	
	public void setActivePlayer(int activePlayer){
		
		for (int i =0; i<16; i++)
		{
			satellites[i].setActivePlayer(activePlayer);
		}
	}
	
	public String getSatelliteXML(){
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("<satellites>");
		
		for (int i=0; i<16; i++){
			builder.append("<satellite>");
				builder.append("<mass>");
				builder.append(satellites[i].getMass(0));
				builder.append("</mass>");
				
				builder.append("<mass>");
				builder.append(satellites[i].getMass(1));
				builder.append("</mass>");
			builder.append("</satellite>");
		}
		
		builder.append("</satellites>");
		
		return builder.toString();
	}
	
	public int satMassCount(int player, int threshold)
	{
		int count = 0;
		
		for (int i=0; i < satellites.length; i++)
		{
			if (satellites[i].getMass(player)>= threshold)
			{
				count++;
			}
		}
		
		return count;
	}
	
	public void reset()
	{
		stolen = 0;
		
		for (int i =0; i<16; i++)
		{
			satellites[i].reset();
		}
	}
	
	public void addMass(int satellite, int mass){
		satellites[satellite].addMass(mass);
	}
	
	public void loadState(int satellite, int mass1, int mass2)
	{
		satellites[satellite].loadState(mass1, mass2);
	}
	
	// TEST FOR AI
	public int getSatelliteX(){
		return satellites[5].getX();
	}
	
	public int getSatelliteY(){
		return satellites[5].getY();
	}
	
	public int getSatelliteMass(int satelliteNumber)
	{
		 return satellites[satelliteNumber].getMass(1);
	}
	
	public void setAi(Boolean aiPlaying){
		for (Satellite sat:satellites){
			sat.setAi(aiPlaying);
		}
	}
}