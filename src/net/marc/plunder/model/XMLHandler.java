package net.marc.plunder.model;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.os.Bundle;

public class XMLHandler extends DefaultHandler{
	
	private static final String TAG = "XmlHandler";
	//static tag Stings??
	//private static final String PLAYER_OPEN = "<player>";
	//private static final String PLAYER_CLOSE = "</player>";
	private Document loadState;
	
	public XMLHandler(){
		loadState = null;
	}
	
	public String getSaveState( int playerNumber, int gamePhase, int activePlayer, Ship[] players, SatelliteContainer satCont)
	{	
		StringBuilder builder = new StringBuilder();
		
	builder.append("<saveFile>");
		builder.append("<gameData>");
		
			//number of players
			builder.append("<numPlayers>");
			builder.append(playerNumber);
			builder.append("</numPlayers>");
			
			//gamePhase
			builder.append("<gamePhase>");
			builder.append(gamePhase);
			builder.append("</gamePhase>");
			
			//active player
			builder.append("<activePlayer>");
			builder.append(activePlayer);
			builder.append("</activePlayer>");
		
		builder.append("</gameData>");
			
		//players tags
		builder.append("<players>");
		
			//player 1 progress
			builder.append("<player>");
				builder.append("<mass>");
				builder.append(players[0].getMass());
				builder.append("</mass>");
			
				builder.append("<score>");
				builder.append(players[0].getScore());
				builder.append("</score>");
				builder.append("</player>");
		
			//player2 progress	
			builder.append("<player>");
				builder.append("<mass>");
				builder.append(players[1].getMass());
				builder.append("</mass>");
		
				builder.append("<score>");
				builder.append(players[1].getScore());
				builder.append("</score>");
			builder.append("</player>");
			
		builder.append("</players>");
		
		//satellites tags
		builder.append(satCont.getSatelliteXML());
	builder.append("</saveFile>");
	
		return builder.toString();
	}
	
	public void loadData(Bundle save)
	{
		String xmlString = save.getString("save");
		
		Log.d(TAG, xmlString);
		//http://p-xr.com/android-tutorial-how-to-parseread-xml-data-into-android-listview/
		Document doc = null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
				
				InputSource is  = new InputSource();
				is.setCharacterStream(new StringReader(xmlString));
				doc = db.parse(is);
		} catch (ParserConfigurationException e){
			Log.e(TAG, "XML parse error:" + e.getMessage());
		} catch (SAXException e){
			Log.e(TAG,"Wrong XML file structure: "+e.getMessage());
		}catch (IOException e){
			Log.e(TAG, "I/O exception: "+e.getMessage());
		}
		
		loadState = doc;
		/*NodeList foo = doc.getElementsByTagName("mass");
		Log.d(TAG, Integer.toString(foo.getLength()));
		xmlString="hello";*/
	}

	public Bundle loadSaveState(Ship[] players, SatelliteContainer satCont)
	{
		Element root = loadState.getDocumentElement();
		
		// LOAD GAMESTATE STUFF
		NodeList gameNL = root.getElementsByTagName("gameData");
		Element gameData = (Element)gameNL.item(0);
		
		NodeList numPlayersNL = gameData.getElementsByTagName("numPlayers");
		int numPlayers = Integer.parseInt(numPlayersNL.item(0).getTextContent());
		
		NodeList phaseNL = gameData.getElementsByTagName("gamePhase");
		int phase = Integer.parseInt(phaseNL.item(0).getTextContent());
		
		NodeList activeNL = gameData.getElementsByTagName("activePlayer");
		int active = Integer.parseInt(activeNL.item(0).getTextContent());
		
		Bundle bundle = new Bundle();					// bundle to send back to game
		bundle.putInt("playerNumber", numPlayers);
		bundle.putInt("gamePhase", phase);
		bundle.putInt("activePlayer", active);
		
		
		// LOAD PLAYER STUFF
		
		NodeList playerNL = root.getElementsByTagName("player");
		
		for (int i=0; i<2; i++)
		{	
			Element player = (Element)playerNL.item(i);
			NodeList scoreNL = player.getElementsByTagName("score");	// score
			
			int score = Integer.parseInt(scoreNL.item(0).getTextContent());
			
			NodeList massNL = player.getElementsByTagName("mass");	// mass
			
			int mass = Integer.parseInt(massNL.item(0).getTextContent());
			
			players[i].loadState(score, mass);
		}
		
		// LOAD SATELLITE STUFF
		
		satCont.setActivePlayer(active);
		
		NodeList satelliteNL = root.getElementsByTagName("satellite");
		
		for (int i =0; i<16 ; i++)
		{
			Element satellite = (Element)satelliteNL.item(i);
			NodeList massNL = satellite.getElementsByTagName("mass");
			
			int mass1 = Integer.parseInt(massNL.item(0).getTextContent());
			int mass2 = Integer.parseInt(massNL.item(1).getTextContent());
			
			satCont.loadState(i, mass1, mass2);
			//satCont.loadState(i, 10, 10);
		}
		
		return bundle;
	}
	
	
}
