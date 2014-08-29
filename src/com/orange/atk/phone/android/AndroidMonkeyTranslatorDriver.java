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
 * File Name   : AndroidMonkeyTranslatorDriver.java
 *
 * Created     : 07/07/2014
 * Author(s)   : François Jégou
 */
package com.orange.atk.phone.android;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.manageListener.IPhoneKeyListener;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.TcpdumpLineListener;
import com.orange.atk.phone.android.wizard.AndroidWizard;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;
import org.jfree.ui.about.SystemPropertiesTableModel;


/**
 * Generation of a Monkey script file for offline monitoring.
 * @author François Jégou
 *
 */

public class AndroidMonkeyTranslatorDriver implements PhoneInterface  {
    private final String localScript = Platform.TMP_DIR+"scriptfile";
    private String workingDirectory = "";
	/*protected String EVENT_KEY = "";
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

*/


    //public AndroidMonkeyTranslatorDriver(String phoneModel, String version, IDevice d) throws PhoneException {
    public AndroidMonkeyTranslatorDriver() {
        init ();
    }

    private void init()  {
    }

    public void setWorkingDirectory(String wd) {
        workingDirectory=wd;

    }
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /* Implemented script commands */

    public void startMainLog(){
        try {
            FileWriter fw=null;
            fw = new FileWriter(new File((workingDirectory+"/scriptfile.monkey")));
            fw.write("type= raw event\n");
            fw.write("count= 3\n");
            fw.write("speed= 1.0\n");
            fw.write("start data >>\n");
            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",localScript);
            ErrorManager.getInstance().addError(getClass().getName(), error, e);
            //throw new PhoneException(error);
        }

    }

    public void sleep(int time) throws PhoneException {
        FileWriter fw_all=null;
        try {
            Logger.getLogger(this.getClass()).debug("Generating monkey scriptfile : "+workingDirectory+"/scriptfile.monkey");

            fw_all = new FileWriter(new File(workingDirectory+"/scriptfile.monkey"), true);
            fw_all.write("UserWait("+time+")\n");
        } catch (IOException e) {
            String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",workingDirectory+"/scriptfile.monkey");
            ErrorManager.getInstance().addError(getClass().getName(), error, e);
            throw new PhoneException(error);
        } finally{
            try {
                if(fw_all!=null)
                    fw_all.close();
            } catch (IOException e) { }
        }
    }

    public void touchScreenPress(Position click) throws PhoneException {
        FileWriter fw_all=null;
        try {
            Logger.getLogger(this.getClass()).debug("Generating monkey scriptfile : "+workingDirectory+"/scriptfile.monkey");

            fw_all = new FileWriter(new File(workingDirectory+"/scriptfile.monkey"), true);
            fw_all.write("DispatchPointer(0,0,0,"+click.getX()+".0,"+click.getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
            fw_all.write("UserWait("+click.getTime()+")\n");
            fw_all.write("DispatchPointer(0,0,1,"+click.getX()+".0,"+click.getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
        } catch (IOException e) {
            String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",workingDirectory+"/scriptfile.monkey");
            ErrorManager.getInstance().addError(getClass().getName(), error, e);
            throw new PhoneException(error);
        } finally{
            try {
                if(fw_all!=null)
                    fw_all.close();
            } catch (IOException e) { }
        }
    }

    @Override
    /**
     * Perform a slide
     * @param path
     * @throws PhoneException
     */
    public void touchScreenSlide(List<Position> path) throws PhoneException  {
        FileWriter fw_all=null;
        int i=0;

        if(path.size()<2)
            throw new PhoneException("No enough coordinate in path");
        int lastX,lastY;
        try {
            fw_all = new FileWriter(new File(workingDirectory+"/scriptfile.monkey"), true);
            while (i+1<path.size()){
                fw_all.write("captureDrag("+path.get(i).getX()+","+path.get(i).getY()+","+path.get(i+1).getX()+","+path.get(i+1).getY()+",5)\n");
                i++;
            }
        }
        catch (IOException e) {
            String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",localScript);
            ErrorManager.getInstance().addError(getClass().getName(), error, e);
        }finally{
            try {
                if(fw_all!=null)
                    fw_all.close();
            } catch (IOException e) { }
        }
    }

    /* Not yet implemented script commands
    *   (either not applicable or to be implemented when necessary)
    *   */

    protected String getMouseDownCommand(int x, int y) {
        return "error";/*
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
*/
    }

    protected String getMouseMoveCommand(int x, int y) {
        return "error";
/*
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
*/
    }

    protected String getMouseUpCommand(int x, int y) {
        return "error";/*
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
*/
    }

    private String getXYCommand(int x, int y) {
        return "error";/*
		String XY_COMMAND = "";
		if (!EVENT_XY.equals("")) 
			XY_COMMAND = EVENT_XY+((int)((((((float)x*SCREEN_RATIO_X)+32768)*65536)+((float)y*SCREEN_RATIO_Y))))+";";
		else XY_COMMAND = EVENT_X+((int)((float)x*SCREEN_RATIO_X))+";"+EVENT_Y+((int)((float)y*SCREEN_RATIO_Y))+";";
		return XY_COMMAND;
*/
    }

    @Override
    public void keyDown(String key) throws PhoneException {
        System.out.println("key : "+key);


/*
		int code = keyMap.get(key);
		if (keyCanal.get(key).equals("keyboard")) executeShellCommand(EVENT_KEY+code+" 1", false);
		else if (keyCanal.get(key).equals("keyboard2")) executeShellCommand(EVENT_KEY2+code+" 1", false);
		else executeShellCommand(EVENT_KEY3+code+" 1", false);
*/
    }

    @Override
    public void keyPress(String key, int keyPressTime, int delay) throws PhoneException {
/*
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
*/
    }

    @Override
    public void keyUp(String key) throws PhoneException {
/*
		int code =  keyMap.get(key);
		if (keyCanal.get(key).equals("keyboard")) executeShellCommand(EVENT_KEY+code+" 0", false);
		else if (keyCanal.get(key).equals("keyboard2")) executeShellCommand(EVENT_KEY2+code+" 0", false);
		else executeShellCommand(EVENT_KEY3+code+" 0", false);

*/
    }


    @Override
    public  void mouseDown(int x, int y) throws PhoneException {
/*
		//Logger.getLogger(this.getClass() ).debug("MOUSEDOWN("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEDOWN="+getMouseDownCommand(x,y));
		executeShellCommand(getMouseDownCommand(x,y), false);
*/
    }

    @Override
    public  void mouseUp(int x, int y) throws PhoneException {
/*
		//Logger.getLogger(this.getClass() ).debug("MOUSEUP("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEUP="+getMouseUpCommand(x,y));
		executeShellCommand(getMouseUpCommand(x,y), false);
*/
    }

    public void mouseMove(int x, int y) throws PhoneException {
/*
		//Logger.getLogger(this.getClass() ).debug("MOUSEMOVE("+x+","+y+")");
		//Logger.getLogger(this.getClass() ).debug("MOUSEMOVE="+getMouseMoveCommand(x,y));
		executeShellCommand(getMouseMoveCommand(x,y), false);
*/

    }


    @Override
    public void touchScreenDragnDrop(List<Position> path) throws PhoneException {
/*
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
*/
    }

    protected EventFilter createTouchScreenEventFilter(AndroidDriver d, AndroidConfHandler gestionnaire, HashMap<String,Position> softkeyMap){
        return new DefaultTouchScreenEventfilter(d,gestionnaire, softkeyMap);
    }

    protected EventFilter createKeyboardEventFilter(AndroidPhone aphone,Map<Integer, String> codemap){
        return new DefaultKeyboardEventfilter(aphone, codemap);
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void startRecordingMode() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    @Override
    public boolean isInRecordingMode() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return false;
    }

    /* method not applicable for Monkey Translation */
    public void stopRecordingMode() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    @Override
    public void fireStdOutput(String Stdoutput) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void fireLongValue(long newValue, String key) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void fireFloatValue(float newMemValue, String key) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public IMeasureListener[] getPerfListeners() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new IMeasureListener[0];
    }

    @Override
    public void addPerfListener(IMeasureListener listener) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void removePerfListener(IMeasureListener listener) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public IPhoneKeyListener[] getKeyListeners() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new IPhoneKeyListener[0];
    }

    @Override
    public void addPhoneKeyListener(IPhoneKeyListener listener) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void removePhoneKeyListener(IPhoneKeyListener listener) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public String[] getRandomTestList() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new String[0];
    }

    @Override
    public String[] getMonitorList() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new String[0];
    }

    @Override
    public HashMap<String, String> getKeys() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return null;
    }

    @Override
    public String[] getKeyLayouts() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new String[0];
    }

    @Override
    public String[] getRecordPhoneMode() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return new String[0];
    }

    @Override
    public HashMap<String, Long> getResources(List<String> sampledKeys) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return null;
    }

    @Override
    public int getType() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return 0;
    }

    @Override
    public String getIncludeDir() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return null;
    }

    @Override
    public String getConfigFile() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return null;
    }

    @Override
    public String getPhoneConfigFile() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return null;
    }

    @Override
    public boolean isDisabledPhone() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
      return false;
    }

    @Override
    public void sendCommandToExecuteToSolo(Object[] commands) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    @Override
    public void setApkToTestWithRobotiumParam(String packName, String activityName, String packsourceDir, int versionCode) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public ArrayList<String> getAllInstalledAPK() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return null;
    }

    @Override
    public ArrayList<String> getForegroundApp() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return null;
    }

    @Override
    public String getSerialNumber() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return null;
    }

    @Override
    public void pullData(String source, String destination) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    /* method not applicable for Monkey Translation */
    @Override
    public void beep() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void runMidlet(String midlet) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void killMidlet(String midlet) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public String getCurrentMidlet() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return "error";}

    /* method not applicable for Monkey Translation */
    @Override
    public boolean isMidletRunning(String MidletName) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return false;}

    /* method not applicable for Monkey Translation */
    @Override
    public void reset() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void useCpu(int percentUse) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void fillStorage(long fillSpace) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void freeStorage() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public BufferedImage screenShot() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return new BufferedImage(0,0,0);}

    /* method not applicable for Monkey Translation */
    @Override
    public String getName() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return "MonkeyDriver";}

    /* method not applicable for Monkey Translation */
    @Override
    public String getUID() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return "AndroidMonkeyDriver";}

    /* method not applicable for Monkey Translation */
    @Override
    public void disableUSBcharge() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void setOrientation(int direction) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void sendEmail(String Subject, String Msg, String EmailDest, String NameDest,
                          String NameSrc, String EmailSrc) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void setSleepMode(boolean issleep) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void startTestingMode() throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public void startTestingMode(String directory, String configFile) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    /* method not applicable for Monkey Translation */
    @Override
    public boolean isInTestingMode() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return false;}

    /* method not applicable for Monkey Translation */
    @Override
    public void stopTestingMode() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
    }

    @Override
    public void addTcpdumpLineListener(TcpdumpLineListener listener) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public boolean isDeviceRooted() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return false;
    }

    @Override
    public void setCnxStatus(int status) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public int getCnxStatus() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return 0;
    }

    @Override
    public boolean isFailed() {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");

        return false;
    }

    @Override
    public void setFailed(boolean failed) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public boolean startRandomTest(String hopperTest, String outputDir, ResultLogger mainLogger, Map<String, String> hopperTestParam) throws PhoneException {

        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");
        return false;
    }

    @Override
    public void setvariable(String testFile, String outputDir) {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void setFlightMode(boolean on) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

    @Override
    public void sendSMS(String PhoneNumber, String Msg) throws PhoneException {
        Logger.getLogger(this.getClass()).info("AndroidMonkeyTranslatorDriver : method not implemented ");


    }

}
