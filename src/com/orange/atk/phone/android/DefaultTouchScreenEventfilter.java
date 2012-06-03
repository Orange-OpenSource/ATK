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
 * File Name   : DefaultTouchScreenEventfilter.java
 *
 * Created     : 26/11/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.util.Position;


/**
 * Class used to filter output of very common touch screen,
 * He is found on HTC G1, Samsung SPICA , MOtorola Morisson...
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class DefaultTouchScreenEventfilter extends EventFilter {

	protected int ALLOW_PIXEL_MOVE = 10;	
	protected int ALLOW_SOFTKEY_MOVE = 20;	

	//code to detect movement
	protected static  String X_POSITION="xxx";
	protected static  String Y_POSITION="xxx";
	protected static  String XY_POSITION="xxx";
	protected static  String FLUSH_EVENT="xxx";
	protected static  String FLUSH_EVENT2="xxx";

	/**
	 * Pattern corresponding to a touch down event.<p>
	 * When the phone returns a range of value corresponding to the pressure,
	 * this parameter must be set to the <b>smallest possible value</b>.
	 */
	protected static String MOUSE_DOWN;
	/**
	 * Pattern corresponding the max of a touch down event.<p>
	 * When the phone returns a range of value corresponding to the pressure,
	 * this parameter must be set to the <b>biggest possible value</b>.
	 * Otherwise, this parameter is ignored and set to null.
	 */
	protected static String MOUSE_DOWN_MAX = null;
	/**
	 * When the phone returns a range of value corresponding to the pressure,
	 * this parameter is set to the <b>smallest suffix</b> 
	 * of the pattern corresponding to a touch down.
	 * Otherwise, this parameter is ignored and set to 0.
	 */
	protected static int MOUSE_DOWN_SUFFIX_MIN = 0;
	/**
	 * When the phone returns a range of value corresponding to the pressure,
	 * this parameter is set to the <b>biggest suffix</b>
	 * of the pattern corresponding to a touch down.
	 * Otherwise, this parameter is ignored and set to 0.
	 */
	protected static int MOUSE_DOWN_SUFFIX_MAX = 0;

	/**
	 * Pattern corresponding to a touch up event.
	 */	
	protected static String MOUSE_UP;


	//List of Position 
	private List<Position> tempmoveposition;
	//Discriminate if the action is a Drag or a Slide
	private boolean isDragAndDrop=false;
	private boolean hasMoved=false;

	//time of last action such as move Mouse Down or First move
	private long refTime=0;

	AndroidDriver driver;
	private Action action=Action.UNKNOW_ACTION;
	protected int x=0;
	protected int y=0;
	protected int moy_x=0;
	protected int moy_y=0;
	protected int counter = 0;
	private Position oldPosition;
	private Position initialPosition;
	private Action oldAction = Action.UNKNOW_ACTION;
	private HashMap<String, Position> softkeyMap;
	private boolean flushSent = false;
	private boolean sendFlushTwiceForMouseUp = false;

	/**
	 * Parse a 8 character hexadecimal string into an int. 
	 * Returns -1 instead of NumberFormatException when value="FFFFFFFF"
	 */
	public static int parseInt(String value) {
		int length = value.length();
		int shift = 0;
		long result = 0;
		for (int i = length-1; i >= 0; i--) {
			int digit = Character.digit(value.charAt(i), 16);
			if (digit < 0) {
				throw new NumberFormatException("unable to parse: '" + value + "' (invalid char at : " + i + ")");
			}
			result += ((long)digit) << shift;
			shift += 4;
		}
		return (int)result;
	}
	
	public DefaultTouchScreenEventfilter(AndroidDriver aphone, AndroidConfHandler ges, HashMap<String, Position> softkeyMap) {
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
		MOUSE_DOWN = convertToHexa(ges.getDownpattern() );
		if(ges.getDownMaxpattern()!=null){
			MOUSE_DOWN_MAX = convertToHexa(ges.getDownMaxpattern());
			MOUSE_DOWN_SUFFIX_MAX = Integer.parseInt(MOUSE_DOWN_MAX.substring(10,18), 16);
			MOUSE_DOWN_SUFFIX_MIN = Integer.parseInt(MOUSE_DOWN.substring(10,18), 16);
		}
		MOUSE_UP = convertToHexa(ges.getUppattern() );
		if (ges.getFlushpattern()!=null) FLUSH_EVENT = convertToHexa(ges.getFlushpattern() );
		if (ges.getFlush2pattern()!=null) FLUSH_EVENT2 = convertToHexa(ges.getFlush2pattern() );
		ALLOW_PIXEL_MOVE = ges.getMoveThreshold();
		sendFlushTwiceForMouseUp = ges.sendFlushTwiceForMouseUp();

		Logger.getLogger(this.getClass() ).debug("DefaultTouchScreenEventfilter");
	}


	String convertToHexa(String dec) {
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


	public enum Action {
		MOUSE_DOWN, MOUSE_UP, MOUSE_MOVE,UNKNOW_ACTION//,MOUSE_DOWN_SLIDE,MOUSE_DOWN_DRAG
	}


	@Override	
	public void processline( String line) {
		Logger.getLogger(this.getClass() ).debug("processline : "+line);
		String commands[] = line.split(": ");
		String command = commands[1];
		String timeTable[] = commands[0].split("-");
		//timeTable[0]: time in s - timeTable[1]: time in microsecond
		//time in ms:
		Long time = Long.parseLong(timeTable[0])*1000+Long.parseLong(timeTable[1])/1000;
		
		if (command.startsWith(FLUSH_EVENT) ) {
			if (flushSent) {
				if (sendFlushTwiceForMouseUp) action= Action.MOUSE_UP;
				flushSent = false;
			} else flushSent = true;
			checkAction(time);

		} else if (!command.startsWith(FLUSH_EVENT2)) {
			flushSent = false;
			if (command.startsWith(MOUSE_DOWN)) {
	
				if (oldAction == Action.MOUSE_DOWN || oldAction ==  Action.MOUSE_MOVE ) 
					action = Action.MOUSE_MOVE;
				else 
					action= Action.MOUSE_DOWN;
	
			}else if(command.startsWith(MOUSE_UP) ) {
				if (oldAction != Action.MOUSE_MOVE && oldAction != Action.MOUSE_DOWN) action= Action.UNKNOW_ACTION;	
				else action= Action.MOUSE_UP;
				
			}else if(command.startsWith(X_POSITION)) {
				//Logger.getLogger(this.getClass() ).debug("Command X="+command);
				x= (((int) ((float)Long.parseLong(command.substring(10),16)/driver.SCREEN_RATIO_X)));
			}else if(command.startsWith(Y_POSITION)) {
				//Logger.getLogger(this.getClass() ).debug("Command Y="+command);
				y= (int) ((float)Long.parseLong(command.substring(10),16)/driver.SCREEN_RATIO_Y);
			}else if(command.startsWith(XY_POSITION)) {
				x= (int) ((float)(Long.parseLong(command.substring(10,14),16) - Long.parseLong("8000",16))/driver.SCREEN_RATIO_X);
				y= (int) ((float)Long.parseLong(command.substring(14),16)/driver.SCREEN_RATIO_Y);
				checkAction(time);
			}else if (MOUSE_DOWN_MAX!=null){
				if(command.startsWith(MOUSE_DOWN_MAX.substring(0, 10))) {
					String endCommand = command.substring(10,18);
					Integer valueCommand = parseInt(endCommand);
					//Integer valueCommand = Integer.parseInt(endCommand);
					if(valueCommand>=MOUSE_DOWN_SUFFIX_MIN && valueCommand<=MOUSE_DOWN_SUFFIX_MAX){
						if (oldAction == Action.MOUSE_DOWN || oldAction ==  Action.MOUSE_MOVE ) 
							action = Action.MOUSE_MOVE;
						else 
							action= Action.MOUSE_DOWN;
					} else if (valueCommand<MOUSE_DOWN_SUFFIX_MIN) { // We consider then that it is MOUSE UP event
						if (oldAction != Action.MOUSE_MOVE && oldAction != Action.MOUSE_DOWN) action= Action.UNKNOW_ACTION;	
						else action= Action.MOUSE_UP;									
					}
				}
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
	private void checkAction(Long time) {
		if (action == Action.UNKNOW_ACTION && (oldAction == Action.MOUSE_DOWN || oldAction ==  Action.MOUSE_MOVE )) 
			action = Action.MOUSE_MOVE;
		switchaction( x, y, action, time);	
		oldAction = action;
		action= Action.UNKNOW_ACTION;
		
		
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


}
