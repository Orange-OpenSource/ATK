/*
 * Software Name : ATK
 *
 * Copyright (C) 2007 - 2012 France Télécom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------
 * File Name   : SmartTouchScreenEventfilter.java
 *
 * Created     : 05/08/2011
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.wizard.EventTimerThread;
import com.orange.atk.phone.android.wizard.TimeOutListener;
import com.orange.atk.util.Position;


/**
 * Class used to filter output of very common touch screen,
 * He is found on HTC G1, Samsung SPICA , MOtorola Morisson...
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class SmartTouchScreenEventfilter extends EventFilter implements TimeOutListener {

	EventTimerThread eventTimerThread;
	boolean up = true;

	protected int ALLOW_PIXEL_MOVE = 10;	
	protected int ALLOW_SOFTKEY_MOVE = 20;	

	//code to detect movement
	protected static  String X_POSITION="xxx";
	protected static  String Y_POSITION="xxx";
	protected static  String XY_POSITION="xxx";
	protected static String MOUSE_UP;

	//List of Position 
	private List<Position> tempmoveposition;
	//Discriminate if the action is a Drag or a Slide
	private boolean isDragAndDrop=false;
	private boolean hasMoved=false;

	//time of last action such as move Mouse Down or First move
	private long refTime=0;

	AndroidDriver driver;
	protected int newX=-1;
	protected int newY=-1;
	protected int x=0;
	protected int y=0;
	protected int moy_x=0;
	protected int moy_y=0;
	protected int counter = 0;
	private Position oldPosition;
	private Position initialPosition;
	private HashMap<String, Position> softkeyMap;
	private Long time;
	private boolean mouseUpReceived = false;
	
	public SmartTouchScreenEventfilter(AndroidDriver aphone, AndroidConfHandler ges, HashMap<String, Position> softkeyMap) {
		driver = aphone;
		tempmoveposition = new ArrayList<Position>();
		this.softkeyMap = softkeyMap;
		//réecriture en hexa
		if (ges.getXpattern()!=null) {
			X_POSITION = convertToHexa(ges.getXpattern() );
			Y_POSITION = convertToHexa(ges.getYpattern() );
		} else {
			XY_POSITION = convertToHexa(ges.getXYpattern() );
		}
		ALLOW_PIXEL_MOVE = ges.getMoveThreshold();
		
		MOUSE_UP = convertToHexa(ges.getUppattern() );

		eventTimerThread = new EventTimerThread(this, 500);
		eventTimerThread.start();

		Logger.getLogger(this.getClass() ).debug("DefaultTouchScreenEventfilter");
	}

	public enum Action {
		MOUSE_DOWN, MOUSE_UP, MOUSE_MOVE, UNKNOWN_ACTION
	}

	String convertToHexa(String dec) {
		if (dec == null) return null;
		String result="";
		String[] X_POSITIONs = dec.split(" ");
		int position=0;
		for(String element : X_POSITIONs){
			try{
				position++;
				long x = Integer.parseInt(element);
				String hexel = Long.toHexString(x);

				if(position==3) {
					for(int i=hexel.length() ; i<8 ;i++) 
						hexel = "0"+hexel;

				} else {
					for(int i=hexel.length() ; i<4 ;i++) 
						hexel = "0"+hexel;
				}

				//don't start with space.
				if(position!=1)
					result+=" ";
				result+=hexel;
			}catch (Exception e) {
				Logger.getLogger(this.getClass()).warn("error during conversion command in hexa", e);
			}
		}
		Logger.getLogger(this.getClass()).debug(dec+"-->"+result);

		return result;
	}




	@Override	
	public void processline( String line) {
		Logger.getLogger(this.getClass() ).debug("processline : "+line);
		eventTimerThread.newEventTime(System.currentTimeMillis());
		String commands[] = line.split(": ");
		String command = commands[1];
		String timeTable[] = commands[0].split("-");
		//timeTable[0]: time in s - timeTable[1]: time in microsecond
		//time in ms:
		time = Long.parseLong(timeTable[0])*1000+Long.parseLong(timeTable[1])/1000;
		
		if(command.startsWith(X_POSITION)) {
			//Logger.getLogger(this.getClass() ).debug("Command X="+command);
			newX= (((int) ((float)Long.parseLong(command.substring(10),16)/driver.SCREEN_RATIO_X)));
		}else if(command.startsWith(Y_POSITION)) {
			//Logger.getLogger(this.getClass() ).debug("Command Y="+command);
			newY= (int) ((float)Long.parseLong(command.substring(10),16)/driver.SCREEN_RATIO_Y);
		}else if(command.startsWith(XY_POSITION)) {
			newX= (int) ((float)(Long.parseLong(command.substring(10,14),16) - Long.parseLong("8000",16))/driver.SCREEN_RATIO_X);
			newY= (int) ((float)Long.parseLong(command.substring(14),16)/driver.SCREEN_RATIO_Y);
		}else if (MOUSE_UP != null && command.startsWith(MOUSE_UP)) {
			mouseUpReceived = true;
		}
		
		if (newX!=-1 && newY!=-1) {
			if (newX!=0 && newX !=0) {
				x = newX;
				y = newY;
			}
			newX = -1;
			newY = -1;
			if (up) {
				Logger.getLogger(this.getClass() ).debug("is down");
				switchaction( x, y, Action.MOUSE_DOWN, time);	
				up = false;
			} else {
				Logger.getLogger(this.getClass() ).debug("is moving");
				switchaction( x, y, Action.MOUSE_MOVE, time);	
			}
		}
	}

	private void perform_average() {
		if (moy_x == 0) moy_x = x;
		else moy_x = ((moy_x * counter) + x )/ (counter+1);
		counter++;
		if (moy_y == 0) moy_y = y;
		else moy_y = ((moy_y * counter) + y )/ (counter+1);
		counter++;
		
	}
	
	private void switchaction(int x,int y, Action action, Long time)
	{
		switch(action) {
		case MOUSE_UP:
			Logger.getLogger(this.getClass() ).debug("MOUSE UP x="+x+" - y="+y+" - time="+time);
			if(hasMoved) {
				tempmoveposition.add(new Position(x,y,(time-refTime)));
				if(isDragAndDrop){
					driver.phoneTouchScreenDragndrop(tempmoveposition);
				} else {
					driver.phoneTouchScreenSlide(tempmoveposition);
				}
			} else {
				perform_average();
				boolean isSoftKeyPress = false;
				Iterator<String> keys = softkeyMap.keySet().iterator();
				while (keys.hasNext() && !isSoftKeyPress) {
					String key = keys.next();
					Position pos = softkeyMap.get(key);
					int key_x = pos.getX();
					int key_y = pos.getY();
					if ((key_x-ALLOW_SOFTKEY_MOVE < moy_x ) && (moy_x < key_x+ALLOW_SOFTKEY_MOVE) && (key_y-ALLOW_SOFTKEY_MOVE < moy_y ) && (moy_y < key_y+ALLOW_SOFTKEY_MOVE)) {
						driver.phoneKey(key, (int) (time - refTime), 0);
						isSoftKeyPress = true;
					}
				}
				if (!isSoftKeyPress) driver.phoneTouchScreenPressed(moy_x, moy_y,time - refTime);
			}

			//FLUSH temp
			tempmoveposition = new ArrayList<Position>();
			isDragAndDrop = false;
			hasMoved=false;
			initialPosition=null;
			oldPosition=null;
			moy_x = 0;
			moy_y = 0;
			counter = 0;
			break;
		case MOUSE_DOWN:
			//Start Timer
			Logger.getLogger(this.getClass() ).debug("MOUSE DOWN x="+x+" - y="+y+" - time="+time);
			perform_average();
			oldPosition =new Position(x,y,0);
			tempmoveposition.add(oldPosition);
			initialPosition = oldPosition;
			refTime = time;
			break;

		case MOUSE_MOVE:
			Logger.getLogger(this.getClass() ).debug("MOUSE MOVE x="+x+" - y="+y+" - time="+time);
			perform_average();
			if (initialPosition==null) {
				oldPosition =new Position(x,y,0);
				tempmoveposition.add(oldPosition);
				initialPosition = oldPosition;
				refTime = time;
			} else {
				if(((Math.abs(initialPosition.getX()-x) >ALLOW_PIXEL_MOVE ||
					  Math.abs(initialPosition.getY()-y) >ALLOW_PIXEL_MOVE ))){
					if (!hasMoved && (time-refTime)>PhoneInterface.TOUCHSCREEN_LONG_EVENT_MIN_TIME )
						isDragAndDrop = true;
					hasMoved=true;
				}			
				
				if(oldPosition!=null&&
						((Math.abs(oldPosition.getX()-x) >ALLOW_PIXEL_MOVE ||
						  Math.abs(oldPosition.getY()-y) >ALLOW_PIXEL_MOVE ))){
							oldPosition =new Position(x,y,(time-refTime));
							tempmoveposition.add(oldPosition);
				}	
			}
			break;
		}	
	}

	public void notifyTimeOut() {
		Logger.getLogger(this.getClass() ).debug("notify timeout");
		if (MOUSE_UP==null || mouseUpReceived) {
			switchaction( x, y, Action.MOUSE_UP, time);	
			up = true;
			mouseUpReceived = false;
		}
	}

	public void stopDetection() {
		eventTimerThread.stopRunning();
	}

}
