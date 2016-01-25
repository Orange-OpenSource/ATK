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
 * File Name   : AndroidDriver.java
 *
 * Created     : 07/12/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.phone.android;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.wizard.AndroidWizard;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.Position;


/**
 * This class is the base for model specific driver.
 * @author Leral Yvain 
 *
 */
public class AndroidDriver extends AndroidPhone implements PhoneInterface {
	protected String EVENT_KEY = "";
	protected String EVENT_KEY2 = "";
	protected String EVENT_KEY3 = "";

	protected float SCREEN_RATIO_X = 1;
	protected float SCREEN_RATIO_Y = 1;
	protected String EVENT_MOUSE_UP = "";
	protected String EVENT_MOUSE_DOWN = "";
	protected String EVENT_MOUSE_MOVE = "";
	protected String EVENT_X="";
	protected String EVENT_Y="";
	protected String EVENT_XY="";
	protected String EVENT_FLUSH2="";
	protected String EVENT_FLUSH="";
	protected String MOUSE_CHANNEL_EVENT="";  
	protected String KEY_CHANNEL_EVENT="";
	protected String KEY_CHANNEL_EVENT2="";
	protected String KEY_CHANNEL_EVENT3="";
	
	//Used for Slide
	private	int maxNumberOfCommand = 6;
	private static final int LONGEST_COMMAND_POSSIBLE_TO_SEND = 1000;
	private static final int MIN_INTERVAL = 15;
	private static final int MAX_INTERVAL = 40;
	
	private int LONG_PRESS_TIME;
	private boolean LONG_PRESS_MULTIPLE = false;
	protected boolean USE_MONKEY_FOR_PRESS = false;
	protected boolean DONT_USE_MONKEY = false;
	
	protected EventFilter eventFilter;
	protected Thread recordingPhoneModethreadTouchscreen;
	protected Thread recordingPhoneModethreadkeyboard;
	protected Thread recordingPhoneModethreadkeyboard2;
	protected Thread recordingPhoneModethreadkeyboard3;
	protected long interval_between_event=70;
	protected HashMap<String, Integer> keyMap;
	private HashMap<String, Position> softkeyMap;
	protected HashMap<String, String> keyCanal;
	protected AndroidConfHandler gestionnaire;



	/**
	 * TEST:TODO verify that's the good solution.
	 * 
	 * @param phoneModel name of the phone to initialize.
	 * @throws PhoneException when phone isn't implemented.
	 */
	public AndroidDriver(String phoneModel, String version, IDevice d) throws PhoneException {
		super(d);
		adevice= d;
        File conffile = getFile(Platform.getInstance().getUserConfigDir(),phoneModel, version);
        if(!conffile.exists()){
            conffile = getFile(Platform.getInstance().getJATKPath(),phoneModel, version);
        }
        Logger.getLogger(this.getClass()).debug("Configuration file :"+conffile.getAbsolutePath());
		if (!conffile.exists()) {
			Logger.getLogger(this.getClass()).warn("No file  found for configuration");
			//new AndroidWizard(this, d,confFileName);
            String defaultConfFileName = Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"conf"+Platform.FILE_SEPARATOR+"default.xml";
            File defaultconffile = new File(defaultConfFileName);
            init(phoneModel,defaultconffile);
			return;
		} else init(phoneModel, conffile);
	}

    private File getFile(String path, String phoneModel, String version) {
        //read the initialization file
        String confFileName = path+Platform.FILE_SEPARATOR+
            "conf"+Platform.FILE_SEPARATOR+phoneModel;
        File conffile = new File(confFileName+"_"+version+".xml");
        if (!conffile.exists()) {
            // try with just version number
            conffile = new File(confFileName+"_"+version.substring(0, 3)+".xml");
            if (!conffile.exists()) {
                // try with just first number of version
                conffile = new File(confFileName+"_"+version.substring(0, 1)+".xml");
                if (!conffile.exists()) {
                    // try with just phone model name
                    conffile = new File(confFileName+".xml");
                }
            }
        }
        return conffile;
    }

    private void init(String phoneModel, File conffile) throws PhoneException {
        mPhoneConfigFile = conffile.getName();
		gestionnaire = new AndroidConfHandler();
		try {
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = fabrique.newSAXParser();

			parseur.parse(conffile,gestionnaire);
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CONFIG_FILE_PARSING_ERROR", phoneModel+".xml"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error); 
		}

		keyMap = gestionnaire.getkeymap();
		softkeyMap = gestionnaire.getsoftkeymap();
		keyCanal = gestionnaire.getkeycanal();
		try {
			adevice.executeShellCommand("getevent", 
					new DetectChannelEventfilter(this,
							gestionnaire.getKeyboardchannel(),
							gestionnaire.getKeyboardSecondchannel(),
							gestionnaire.getkeyboardThirdchannel(),
							gestionnaire.getTouchscreenchannel() ));

		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error); 
		} catch (TimeoutException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		} catch (AdbCommandRejectedException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		} catch (ShellCommandUnresponsiveException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		}


		EVENT_KEY = "sendevent "+KEY_CHANNEL_EVENT+" 1 ";
		EVENT_KEY2 = "sendevent "+KEY_CHANNEL_EVENT2+" 1 ";
		EVENT_KEY3 = "sendevent "+KEY_CHANNEL_EVENT3+" 1 ";
		if (gestionnaire.getXpattern()!=null) {
			EVENT_Y = "sendevent "+MOUSE_CHANNEL_EVENT+" "+gestionnaire.getYpattern();
			EVENT_X = "sendevent "+MOUSE_CHANNEL_EVENT+" "+gestionnaire.getXpattern();
		}
		if (gestionnaire.getXYpattern()!=null) {
			EVENT_XY = "sendevent "+MOUSE_CHANNEL_EVENT+" "+gestionnaire.getXYpattern();
		}
		EVENT_MOUSE_DOWN = "sendevent "+MOUSE_CHANNEL_EVENT+" "+gestionnaire.getDownpattern()+";";
		EVENT_MOUSE_UP = "sendevent "+MOUSE_CHANNEL_EVENT+" "+gestionnaire.getUppattern()+";";
		if (gestionnaire.sendMouseDownForMove()) EVENT_MOUSE_MOVE = EVENT_MOUSE_DOWN;
		String flush = gestionnaire.getFlushpattern();
		if (flush != null) EVENT_FLUSH = "sendevent "+MOUSE_CHANNEL_EVENT+" "+flush+";";
		String flush2 = gestionnaire.getFlush2pattern();
		if (flush2 != null)	EVENT_FLUSH2 = "sendevent "+MOUSE_CHANNEL_EVENT+" "+flush2+";";
		SCREEN_RATIO_X = gestionnaire.getRatioX();
		SCREEN_RATIO_Y = gestionnaire.getRatioY();
			
		LONG_PRESS_TIME = gestionnaire.getLongPressTime();
		LONG_PRESS_MULTIPLE = gestionnaire.getLongPressMultiple();
		USE_MONKEY_FOR_PRESS = gestionnaire.useMonkeyForPress();
		DONT_USE_MONKEY = gestionnaire.dontUseMonkey();
		
		// We determince the size of the commands to know the maximum number of command
		// we can send at once using executeShellCommand
		int lengthCommand = LONGEST_COMMAND_POSSIBLE_TO_SEND;
		lengthCommand -= getMouseDownCommand(100,100).length();
		lengthCommand -= getMouseUpCommand(100,100).length();
		int mouseMoveCommandLenght = getMouseMoveCommand(100,100).length();
		Logger.getLogger(this.getClass() ).debug("getMouseMoveCommand(100,100): " + getMouseMoveCommand(100,100));

		maxNumberOfCommand = lengthCommand/mouseMoveCommandLenght;
		Logger.getLogger(this.getClass() ).debug("Number of commands we can send at once using executeShellCommand: " +
													(maxNumberOfCommand+2));
		this.setDisabledPhone(false);
	}

	protected String getMouseDownCommand(int x, int y) {
		if (gestionnaire.sendMouseEventFirst()) {
			if (gestionnaire.sendSeparateFlush())
				return (EVENT_MOUSE_DOWN+
						EVENT_FLUSH+
						getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH);
			else return (EVENT_MOUSE_DOWN+
					getXYCommand(x,y)+
					EVENT_FLUSH2+
					EVENT_FLUSH);
		} else {
			if (gestionnaire.sendSeparateFlush())
				return (getXYCommand(x,y)+
					EVENT_FLUSH2+
					EVENT_FLUSH+
					EVENT_MOUSE_DOWN+
					EVENT_FLUSH);
			
			else return (getXYCommand(x,y)+
					EVENT_MOUSE_DOWN+
					EVENT_FLUSH2+
					EVENT_FLUSH);

		}
	}

	protected String getMouseMoveCommand(int x, int y) {
		if (gestionnaire.sendMouseEventFirst()) {
			if (gestionnaire.sendSeparateFlush() && !EVENT_MOUSE_MOVE.equals(""))
				return (EVENT_MOUSE_MOVE+
						EVENT_FLUSH+
						getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH);
			else return (EVENT_MOUSE_MOVE+
						getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH);
		} else { 
			if (gestionnaire.sendSeparateFlush() && !EVENT_MOUSE_MOVE.equals(""))
				return (getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH+
						EVENT_MOUSE_MOVE+
						EVENT_FLUSH);
			else return (getXYCommand(x,y)+
					EVENT_MOUSE_MOVE+
					EVENT_FLUSH2+
					EVENT_FLUSH);
		}
	}

	protected String getMouseUpCommand(int x, int y) {
		if (gestionnaire.sendMouseEventFirst()) {
			if (gestionnaire.sendSeparateFlush())
				return (EVENT_MOUSE_UP+
						EVENT_FLUSH+
						getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH);
			else return (EVENT_MOUSE_UP+
					getXYCommand(x,y)+
					EVENT_FLUSH2+
					EVENT_FLUSH);
		} else { 
			if (gestionnaire.sendSeparateFlush())
				return (getXYCommand(x,y)+
						EVENT_FLUSH2+
						EVENT_FLUSH+
						EVENT_MOUSE_UP+
						EVENT_FLUSH);
			else return (getXYCommand(x,y)+
					EVENT_MOUSE_UP+
					EVENT_FLUSH2+
					EVENT_FLUSH);
		}
	}
	
	private String getXYCommand(int x, int y) {
		String XY_COMMAND = "";
		if (!EVENT_XY.equals("")) 
			XY_COMMAND = EVENT_XY+((int)((((((float)x*SCREEN_RATIO_X)+32768)*65536)+((float)y*SCREEN_RATIO_Y))))+";";
		else XY_COMMAND = EVENT_X+((int)((float)x*SCREEN_RATIO_X))+";"+EVENT_Y+((int)((float)y*SCREEN_RATIO_Y))+";";
		return XY_COMMAND;
	}

	@Override
	public void keyDown(String key) throws PhoneException {
		int code = keyMap.get(key);
		if (keyCanal.get(key).equals("keyboard")) executeShellCommand(EVENT_KEY+code+" 1", false);
		else if (keyCanal.get(key).equals("keyboard2")) executeShellCommand(EVENT_KEY2+code+" 1", false);
		else executeShellCommand(EVENT_KEY3+code+" 1", false);
	}

	@Override
	public void keyPress(String key, int keyPressTime, int delay) throws PhoneException {
		if (keyMap.get(key)==null) super.keyPress(key, keyPressTime, delay);
		else {
			long time = System.currentTimeMillis();
			//Logger.getLogger(this.getClass() ).debug("receive key " +key);
	
			keyDown(key);
			long timeSleep = keyPressTime - (System.currentTimeMillis()- time);
			if (timeSleep>0) {
				try {
					Thread.sleep(timeSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			keyUp(key);
		}
	}

	@Override
	public void keyUp(String key) throws PhoneException {
		int code =  keyMap.get(key);
		if (keyCanal.get(key).equals("keyboard")) executeShellCommand(EVENT_KEY+code+" 0", false);
		else if (keyCanal.get(key).equals("keyboard2")) executeShellCommand(EVENT_KEY2+code+" 0", false);
		else executeShellCommand(EVENT_KEY3+code+" 0", false);

	}


	@Override
	public  void mouseDown(int x, int y) throws PhoneException {
		//Logger.getLogger(this.getClass() ).debug("MOUSEDOWN("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEDOWN="+getMouseDownCommand(x,y));
		executeShellCommand(getMouseDownCommand(x,y), false);
	}

	@Override
	public  void mouseUp(int x, int y) throws PhoneException {
		//Logger.getLogger(this.getClass() ).debug("MOUSEUP("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEUP="+getMouseUpCommand(x,y));
		executeShellCommand(getMouseUpCommand(x,y), false);
	}

	public void mouseMove(int x, int y) throws PhoneException {
		//Logger.getLogger(this.getClass() ).debug("MOUSEMOVE("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEMOVE="+getMouseMoveCommand(x,y));
		executeShellCommand(getMouseMoveCommand(x,y), false);

	}

	@Override
	public void touchScreenPress(Position click) throws PhoneException {
		int x = click.getX();
		int y = click.getY();
		mouseDown(x, y);
		
		//Some phones need to get multiple mouseMove to simulate a long press.
		if(!LONG_PRESS_MULTIPLE){
			try {
				Thread.sleep(click.getTime());
				//Logger.getLogger(this.getClass() ).debug("Sleep: "+click.getTime()+"ms");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			Long startTime = System.currentTimeMillis();
			int delta = 1;
			do{
				try {
					Thread.sleep(200);
					//Logger.getLogger(this.getClass() ).debug("Sleep: 200ms - Time: "+(System.currentTimeMillis()-startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if((System.currentTimeMillis()-startTime)>click.getTime())
					break;
				//We need to change the coordinates (else the command will be ignored)
				delta = -delta;
				executeShellCommand(getMouseMoveCommand(x += delta,y),false);				
			}while(true);
		}
		mouseUp(x, y);
	}

	
	@Override
	/**
	 * Perform an optimized slide sending the whole command at once when possible
	 * in the limit of the maximum command size. 
	 * @param path
	 * @throws PhoneException
	 */
	public void touchScreenSlide(List<Position> path) throws PhoneException  {
		if(path.size()<2)
			throw new PhoneException("No enough coordinate in path");
		int lastX,lastY;
		
		int minDistance;
		int maxDiff = Math.max(Math.abs(path.get(path.size()-1).getX()-path.get(0).getX()),
						Math.abs(path.get(path.size()-1).getY()-path.get(0).getY()));
		minDistance = maxDiff/maxNumberOfCommand;
		if (minDistance>MAX_INTERVAL){
			minDistance = MAX_INTERVAL;
		}
		if (minDistance<MIN_INTERVAL){
			minDistance = MIN_INTERVAL;
		}
		//Logger.getLogger(this.getClass() ).debug("MaxDiff="+maxDiff+" - MinDistance="+minDistance);
		
		int numberOfCommand = 0;
		
		lastX = path.get(0).getX();
		lastY = path.get(0).getY();
		String command = getMouseDownCommand(lastX,lastY);
		numberOfCommand++;
		//Logger.getLogger(this.getClass() ).debug("Add command mouse Down : X="+lastX+" - Y="+lastY);
		for (int i=1 ; i< path.size()-1 ;i++) {	

			if((Math.abs(lastX-path.get(i).getX())>minDistance)||
				(Math.abs(lastY-path.get(i).getY())>minDistance)){
				lastX = path.get(i).getX();
				lastY = path.get(i).getY();
				command += getMouseMoveCommand(lastX,lastY);
				numberOfCommand++;
				//Logger.getLogger(this.getClass() ).debug("Add command mouse Move : X="+lastX+" - Y="+lastY);
				if(numberOfCommand==maxNumberOfCommand){
					executeShellCommand(command,false);
					command = "";
					numberOfCommand = 0;
				}
			}
		}
		command += getMouseUpCommand(path.get(path.size()-1).getX(),path.get(path.size()-1).getY());
		//Logger.getLogger(this.getClass() ).debug("Add command mouse Up : X="+lastX+" - Y="+lastY);
		executeShellCommand(command,false);
	}


	@Override
	public void touchScreenDragnDrop(List<Position> path) throws PhoneException {
		if(path.size()<2)
			throw new PhoneException("No enough coordinate in path");
		int lastX,lastY;
		
		int minDistance;
		int maxDiff = Math.max(Math.abs(path.get(path.size()-1).getX()-path.get(0).getX()),
						Math.abs(path.get(path.size()-1).getY()-path.get(0).getY()));
		minDistance = maxDiff/maxNumberOfCommand; 
		if (minDistance>MAX_INTERVAL){
			minDistance = MAX_INTERVAL;
		}
		if (minDistance<MIN_INTERVAL){
			minDistance = MIN_INTERVAL;
		}
		//Logger.getLogger(this.getClass() ).debug("MaxDiff="+maxDiff+" - MinDistance="+minDistance);
		
		int numberOfCommand = 0;
		
		lastX = path.get(0).getX();
		lastY = path.get(0).getY();
		long lastTime = path.get(0).getTime();
		long lastTimePlayed = System.currentTimeMillis();

		//Logger.getLogger(this.getClass() ).debug("Add command mouse Down : X="+lastX+" - Y="+lastY);
		String command = getMouseDownCommand(lastX,lastY);
		executeShellCommand(command,false);

		//Some phones need to get multiple mouseMove to simulate a long press.
		if(!LONG_PRESS_MULTIPLE){
			try {
				Thread.sleep(LONG_PRESS_TIME);
				//Logger.getLogger(this.getClass() ).debug("Sleep: "+LONG_PRESS_TIME+"ms");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			Long startTime = System.currentTimeMillis();
			int delta = 1;
			do{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if((System.currentTimeMillis()-startTime)>LONG_PRESS_TIME)
					break;
				//We need to change the coordinates (else the command will be ignored)
				lastX += delta;
				delta = -delta;
				command = getMouseMoveCommand(lastX,lastY);
				executeShellCommand(command,false);				
			}while(true);
		}
		
		command = "";
		for (int i=1 ; i< path.size() ;i++) {	
			long diffTime = path.get(i).getTime()-lastTime;
			long diffReal= System.currentTimeMillis()-lastTimePlayed;
			
			if((Math.abs(lastX-path.get(i).getX())>minDistance)||
				(Math.abs(lastY-path.get(i).getY())>minDistance)||
				(i==(path.size()-1))){
				lastX = path.get(i).getX();
				lastY = path.get(i).getY();
				command += getMouseMoveCommand(lastX,lastY);
				//Logger.getLogger(this.getClass() ).debug("Add command mouse Move : X="+lastX+" - Y="+lastY);
				numberOfCommand++;
				if(numberOfCommand==maxNumberOfCommand){
					lastTime = path.get(i).getTime();
					lastTimePlayed = System.currentTimeMillis();
					//Logger.getLogger(this.getClass() ).debug("DiffTime= "+diffTime+" - DiffReal= "+diffReal);
					executeShellCommand(command,false);
					//If we are too fast, we add some sleep
					if((diffTime-diffReal)>100){
						try {
							//Logger.getLogger(this.getClass() ).debug("Sleep: "+(diffTime-diffReal));
							Thread.sleep(diffTime-diffReal);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					command = "";
					numberOfCommand = 0;
				}				
			}
		}
		command += getMouseUpCommand(path.get(path.size()-1).getX(),path.get(path.size()-1).getY());
		//Logger.getLogger(this.getClass() ).debug("Add command mouse Up : X="+(path.get(path.size()-1).getX())+" - Y="+(path.get(path.size()-1).getY()));
		executeShellCommand(command,false);
	}
	
	@Override
	public void stopRecordingMode() {
		if(recordingPhoneModethreadTouchscreen !=null &&  recordingPhoneModethreadTouchscreen.getState() != Thread.State.TERMINATED ) {
			((RecordingThread) recordingPhoneModethreadTouchscreen).stoprecording();
			if (gestionnaire.useSmartTouchDetection() && eventFilter!=null) ((SmartTouchScreenEventfilter)eventFilter).stopDetection();
		}
		if(recordingPhoneModethreadkeyboard !=null &&  recordingPhoneModethreadkeyboard.getState() != Thread.State.TERMINATED ) 
			((RecordingThread) recordingPhoneModethreadkeyboard).stoprecording();

		if(recordingPhoneModethreadkeyboard2 !=null &&  recordingPhoneModethreadkeyboard2.getState() != Thread.State.TERMINATED ) 
			((RecordingThread) recordingPhoneModethreadkeyboard2).stoprecording();

		if(recordingPhoneModethreadkeyboard3 !=null &&  recordingPhoneModethreadkeyboard3.getState() != Thread.State.TERMINATED ) 
			((RecordingThread) recordingPhoneModethreadkeyboard3).stoprecording();


		isScriptRecording = false;
	}

	protected EventFilter createTouchScreenEventFilter(AndroidDriver d, AndroidConfHandler gestionnaire, HashMap<String,Position> softkeyMap){
		return new DefaultTouchScreenEventfilter(d,gestionnaire, softkeyMap);
	}
	
	protected EventFilter createKeyboardEventFilter(AndroidPhone aphone,Map<Integer, String> codemap){
		return new DefaultKeyboardEventfilter(aphone, codemap);
	}
	
	public void startRecordingMode() throws PhoneException {
		isScriptRecording = true;
		//touchscreen

		//filter in mouse move at least 10px of difference
		if (gestionnaire.useSmartTouchDetection()) eventFilter = new SmartTouchScreenEventfilter(this,gestionnaire, softkeyMap);
		else{
			Logger.getLogger(this.getClass() ).debug("Creating touchScreen Event filter");
			eventFilter = createTouchScreenEventFilter(this,gestionnaire, softkeyMap);
		}
		recordingPhoneModethreadTouchscreen = new RecordingThread(adevice,
				MOUSE_CHANNEL_EVENT,
				eventFilter);
		recordingPhoneModethreadTouchscreen.start();



		//keyboard
		//invert keymap
		final HashMap<Integer,String> keycodeMap = new HashMap<Integer, String>();
		for (String key : keyMap.keySet()) 
			keycodeMap.put(keyMap.get(key), key);

		recordingPhoneModethreadkeyboard = new RecordingThread(adevice,
				KEY_CHANNEL_EVENT,
				createKeyboardEventFilter(this, keycodeMap) );
		recordingPhoneModethreadkeyboard.start();

		if (KEY_CHANNEL_EVENT2!=null) {
			recordingPhoneModethreadkeyboard2 = new RecordingThread(adevice,
				KEY_CHANNEL_EVENT2,
				createKeyboardEventFilter(this, keycodeMap) );
			recordingPhoneModethreadkeyboard2.start();
		}
		if (KEY_CHANNEL_EVENT3!=null) {
			recordingPhoneModethreadkeyboard3 = new RecordingThread(adevice,
				KEY_CHANNEL_EVENT3,
				createKeyboardEventFilter(this, keycodeMap));
			recordingPhoneModethreadkeyboard3.start();
		}		
		return;

	}	

}
