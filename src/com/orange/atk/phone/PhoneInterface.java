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
 * File Name   : PhoneInterface.java
 *
 * Created     : 28/01/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.manageListener.IPhoneKeyListener;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;

/**
 * Phone interface for the interpreter.
 */

public interface PhoneInterface {
	/**
	 * Default orientation of the screen, used by {@link #setOrientation(int)}
	 */
	public static final int DMDO_DEFAULT = 0;
	/**
	 * 90 deg rotation of the screen, used by {@link #setOrientation(int)}
	 */
	public static final int DMDO_90 = 1;
	/**
	 * 180 deg rotation of the screen, used by {@link #setOrientation(int)}
	 */
	public static final int DMDO_180 = 2;
	/**
	 * 270 deg rotation of the screen, used by {@link #setOrientation(int)}
	 */
	public static final int DMDO_270 = 3;

	public static final int CNX_STATUS_AVAILABLE = 0;
	public static final int CNX_STATUS_BUSY = 1;
	public static final int CNX_STATUS_DISCONNECTED = 2;

	public static final String STATUS_PASSED = "PASSED";
	public static final String STATUS_FAILED = "FAILED";

	/**
	 * Time in ms between two event on touchscreen are recorded. That's also
	 * time to wait for playing a second event on touchscreen.
	 */
	// public static final int INTERVAL_TIME_BETWEEN_TOUCHSCREEN_EVENT = 70;
	public static final int TOUCHSCREEN_LONG_EVENT_MIN_TIME = 600;

	public static final int TYPE_RECORDER = -2;
	public static final int TYPE_DEFAULT = -1;
	public static final int TYPE_ANDROID = 0;
	public static final int TYPE_SE = 1;
	public static final int TYPE_S60 = 2;
	public static final int TYPE_WTK = 3;
	public static final int TYPE_WINMOBILE = 4;
	public static final int TYPE_SAMSUNG = 5;
	public static final int TYPE_MEDIATEK = 6;
	public static final int TYPE_LG = 7;

	public long interval_between_event = 70;
	public float power = 0;
	public String JATKPath = Platform.getInstance().getJATKPath();

	/**
	 * Simulate pressure without loosening on the screen.
	 * 
	 * @param path
	 *            . A list of coordinate which approximate the path of the drag
	 *            n drop.
	 * 
	 * @return true if a mouse down event has been generated, false if an error
	 *         happens
	 * @throws PhoneException
	 * 
	 */
	public void touchScreenSlide(List<Position> path) throws PhoneException;

	/**
	 * Simulate pressure without loosening on the screen.
	 * 
	 * @param path
	 *            . A list of coordinate which approximate the path of the drag
	 *            n drop.
	 * 
	 * @return true if a mouse down event has been generated, false if an error
	 *         happens
	 * @throws PhoneException
	 * 
	 */
	public void touchScreenDragnDrop(List<Position> path) throws PhoneException;

	/**
	 * Simulate a long pressure on the screen
	 * 
	 * @param click
	 *            . position of the click
	 * @throws PhoneException
	 */
	// public void touchScreenLongPress(Position click) throws PhoneException;

	/**
	 * Generate the release of the stylet at the coordinates x,y
	 * 
	 * @param x
	 *            abscisse coordinate
	 * @param y
	 *            ordinate coordinate
	 * @throws PhoneException
	 * 
	 */
	public void mouseUp(int x, int y) throws PhoneException;

	/**
	 * Simulate a simple pressure on the screen
	 * 
	 * @param x
	 *            abscisse coordinate
	 * @param y
	 *            ordinate coordinate
	 * @throws PhoneException
	 * 
	 */
	public void mouseDown(int x, int y) throws PhoneException;
	/**
	 * Simulate a simple pressure on the screen
	 * 
	 * @param click
	 *            position of the click
	 * 
	 * @throws PhoneException
	 */
	public void touchScreenPress(Position click) throws PhoneException;

	/**
	 * Simulate the release of the key pressure
	 * 
	 * @param key
	 *            key pressed
	 * @throws PhoneException
	 * 
	 */
	public void keyUp(String key) throws PhoneException;

	/**
	 * Simulate pressure without loosening on a key
	 * 
	 * @param key
	 *            key pressed
	 * @throws PhoneException
	 * 
	 */
	public void keyDown(String key) throws PhoneException;

	/**
	 * Simulate a Key press
	 * 
	 * @param key
	 *            key pressed
	 * @param keyPressTime
	 *            TODO
	 * @param delay
	 *            TODO
	 * @throws PhoneException
	 * 
	 */
	public void keyPress(String key, int keyPressTime, int delay) throws PhoneException;

	/**
	 * Run an executable (midlet/native application)
	 * 
	 * @param midlet
	 *            executable name to execute *
	 * @throws PhoneException
	 * 
	 */
	public void runMidlet(String midlet) throws PhoneException;

	public void killMidlet(String midlet) throws PhoneException;

	/**
	 * Get the current (foreground) process
	 * 
	 * @return name of the process, null in case of error
	 * @throws PhoneException
	 */
	public String getCurrentMidlet() throws PhoneException;

	/**
	 * Check if middlet still running
	 * 
	 * @return name of the process, null in case of error
	 * @throws PhoneException
	 */
	public boolean isMidletRunning(String MidletName) throws PhoneException;

	/**
	 * Soft reset of the device
	 * 
	 * @throws PhoneException
	 * 
	 */
	public void reset() throws PhoneException;

	/**
	 * Create a new process which use percentUse% of cpu
	 * 
	 * @param percentUse
	 *            percentage of cpu to use (0 : cpu not used - 100 : cpu fully
	 *            used, others processes could not be executed) *
	 * @throws PhoneException
	 * 
	 */
	public void useCpu(int percentUse) throws PhoneException;

	/**
	 * Generate a beep
	 * 
	 * @throws PhoneException
	 */
	public void beep() throws PhoneException;

	/**
	 * fill a part of the MIDP persistent storage.
	 * 
	 * @param fillSpace
	 *            size of the persistent storage to fill in byte. if fillspace
	 *            is negative, free totally the available space. <br/>
	 *            <code>
	 *            	// we have a 1MB to fill <br/>
	 *            	phoneInterface.fillStorage(10);<br/>
	 *             	// we have now 1MB -10B free<br/>
	 *              phoneInterface.fillStorage(1000);<br/>
	 *              // we have now (1MB-10B) -1000B free<br/>
	 *              phoneInterface.fillStorage(phoneInterface.getStorage());<br/>
	 *              // we have now 0B free (no space to save informations)<br/>
	 *              phoneInterface.fillStorage(-1)<br/>
	 *            	// we have a 1MB free           <br/>   
	 *            </code>
	 * @throws PhoneException
	 * 
	 */
	public void fillStorage(long fillSpace) throws PhoneException;

	public void freeStorage() throws PhoneException;

	/**
	 * Take a screenshot of the current display of the mobile
	 * 
	 * @return an ImageInputStream object of the display (encoded in png), null
	 *         if error
	 * @throws PhoneException
	 * @see javax.imageio.stream.ImageInputStream
	 */
	public BufferedImage screenShot() throws PhoneException;

	/**
	 * 
	 * @return A common name of the phone
	 */
	public String getName();

	/**
	 * 
	 * @return A string that can be used as Unique IDentifier for the phone
	 */
	public String getUID();

	/**
	 * Return Current Power consumption
	 * 
	 * @return Exception
	 * @throws PhoneException
	 */

	public void disableUSBcharge() throws PhoneException;

	/**
	 * 0 = DMDO_DEFAULT 1=DMDO_90 2=DMDO_180 3=DMDO_270
	 * 
	 * @param direction
	 *            define the screen orientation
	 * @throws PhoneException
	 */
	public void setOrientation(int direction) throws PhoneException;

	/**
	 * Set Orientation
	 * 
	 * @param base
	 *            base
	 * @param value
	 *            value
	 * @param string
	 *            string
	 * @throws PhoneException
	 */
	// public void registry(String base, String value, String string) throws
	// PhoneException;

	/**
	 * Send Email
	 * 
	 * @param Subject
	 *            Subject
	 * @param Msg
	 *            Message
	 * @param EmailDest
	 *            Email Dest
	 * @param NameDest
	 *            Name Dest
	 * @param NameSrc
	 *            Name Source
	 * @param EmailSrc
	 *            Email Source
	 * @throws PhoneException
	 */

	public void sendEmail(String Subject, String Msg, String EmailDest, String NameDest,
			String NameSrc, String EmailSrc) throws PhoneException;

	/**
	 * Set Sleep mode on/off, to enable/disable phone screen saver.
	 * 
	 * @param ispm
	 *            true to set pm on
	 */
	public void setSleepMode(boolean issleep);

	/**
	 * Used to start the connection between the phone and the program. (create
	 * connection, ...);
	 * 
	 * @return TODO
	 * 
	 * @return true if the connection has been done and if we can start to talk
	 *         with the phone, false otherwise.
	 * @throws PhoneException
	 */
	public void startTestingMode() throws PhoneException;

	/**
	 * boolean indicate if Phone is started
	 * 
	 * @return true if phone correctly set
	 */

	public boolean isInTestingMode();

	/**
	 * Call to stop and clean the connection
	 */
	public void stopTestingMode();

	/**
	 * Add a tcpdump listener
	 * 
	 * @param listener
	 */
	public void addTcpdumpLineListener(TcpdumpLineListener listener);

	/**
	 * Returns whether the device is rooted or not
	 */
	public boolean isDeviceRooted();

	/**
	 * Set Cnx Status
	 * 
	 */
	public void setCnxStatus(int status);

	/**
	 * Returns the phone connection status (busy, available, disconnected)
	 */
	public int getCnxStatus();

	/**
	 * Returns true if phone has failed during test or recording
	 */
	public boolean isFailed();

	/**
	 * Set failed
	 */
	public void setFailed(boolean failed);

	/*****
	 * setvariable only used when application is embedded on the phone such on
	 * nokia S60
	 * 
	 * @param testFile
	 *            testFile
	 * @param hopperTest
	 *            TODO
	 * @throws PhoneException
	 * 
	 * 
	 */

	public boolean startRandomTest(String hopperTest, String outputDir, ResultLogger mainLogger,
			Map<String, String> hopperTestParam) throws PhoneException;

	/*****
	 * setvariable only used when application is embedded on the phone such on
	 * nokia S60
	 * 
	 * @param testFile
	 *            testFile
	 * @param hopperTestParam
	 *            TODO
	 * @param ishopper
	 *            TODO
	 * 
	 * 
	 */

	public void setvariable(String testFile, String outputDir);

	/**
	 * Set FlightMode
	 * 
	 * @param On
	 *            : true Enable flight mode / false disable flight mode
	 * @return true if screen state has changed, false if an error happens
	 * @throws PhoneException
	 */
	public void setFlightMode(boolean on) throws PhoneException;

	/**
	 * Kill a process even if there is no window.
	 * 
	 * @param process
	 *            name of process to kill
	 * @param timeout
	 *            timeout in ms
	 */
	// public void killProcess(String process, int timeout);
	/**
	 * 
	 * Allocate x mb of memory to simulate memory usage
	 * 
	 * @param mb
	 *            memory to allocate in mb
	 * @return true if memory has been allocated, false if an error happens
	 */
	// public void fillMemory(long mb);
	/**
	 * 
	 * define the screen state
	 * 
	 * turn off the Screen x=1 turn on the Screen
	 * 
	 * @param state
	 *            true turn on the screen, false turn off the screen
	 * 
	 */
	// public void setScreen(boolean state);

	/**
	 * 
	 * Turn on/off the device (like pressing the power button)
	 * 
	 * @param state
	 *            true turn on the device, false turn off the device
	 * @return true if the device has been turned on/off, false if an error
	 *         happens
	 */
	// public void setPower(boolean state);

	/**
	 * Send SMS
	 * 
	 * @param PhoneNumber
	 *            destination Phone Number
	 * @param Msg
	 *            SMS Msg
	 * @throws PhoneException
	 */

	public void sendSMS(String PhoneNumber, String Msg) throws PhoneException;

	/**
	 * Setting Phone in Script Recording State
	 * 
	 * @return true if phone correctly set
	 */

	public void startRecordingMode() throws PhoneException;

	/**
	 * boolean indicate if Phone is in Script Recording State
	 * 
	 * @param scriptController
	 * 
	 * @return true if phone correctly set
	 */

	public boolean isInRecordingMode();

	/**
	 * Stop Script Recording State
	 * 
	 */

	public void stopRecordingMode();

	/**
	 * add a new Standard Output in log file
	 * 
	 * @param Stdoutput
	 *            Standard Output
	 * @throws PhoneException
	 */

	public void fireStdOutput(String Stdoutput);

	/**
	 * add a new Asynchronous Measurement (Long)
	 * 
	 * @param newValue
	 *            New Measurement
	 * @param key
	 *            name of Measurement in config file xml
	 */

	public void fireLongValue(long newValue, String key);

	/**
	 * add a new Asynchronous Measurement (Float)
	 * 
	 * @param newValue
	 *            New Measurement
	 * @param key
	 *            name of Measurement in config file xml
	 */

	public void fireFloatValue(float newMemValue, String key);

	/**
	 * Get list of Listener
	 * 
	 */

	public IMeasureListener[] getPerfListeners();

	/**
	 * add a new listener
	 * 
	 * @param listener
	 *            listener
	 */

	public void addPerfListener(IMeasureListener listener);

	/**
	 * Remove listener
	 * 
	 * @param listener
	 *            listener
	 */
	public void removePerfListener(IMeasureListener listener);

	/**
	 * Get list of Listener
	 * 
	 */

	// Used by script recorder
	public IPhoneKeyListener[] getKeyListeners();

	/**
	 * add a new listener
	 * 
	 * @param listener
	 *            listener
	 */

	public void addPhoneKeyListener(IPhoneKeyListener listener);

	/**
	 * Remove listener
	 * 
	 * @param listener
	 *            listener
	 */
	public void removePhoneKeyListener(IPhoneKeyListener listener);

	/**
	 * Get Error Message/Status
	 * 
	 */
	// public String getphoneStatusMsg();

	// Get the list of process/applications that can be tested for random test
	public String[] getRandomTestList();

	// Get the list of process/applications that can be monitored (cpu/memory)
	// during a test
	public String[] getMonitorList();

	/**
	 * Get list of keys
	 * 
	 * The key of the map is name of key (used in keyPress, keyDown, or anywhere
	 * else) The value of the map, is the Ressource Location of the icon.
	 */
	// Used by script recorder emulator
	public HashMap<String, String> getKeys();

	/**
	 * Get Keys layout.
	 * 
	 * This function describes if keys is owned by a common layout.
	 * 9KEY_NAVIGATION represent touch for navigation on standard phone
	 * ANDROID_NAVIGATION are the 5 key on HTC G1 by example. PHONE are number
	 * pad for mainly phone. QWERTY is a qwerty keyboard.
	 * 
	 * @return Possibles values are 9KEY_NAVIGATION, ANDROID_NAVIGATION, PHONE,
	 *         QWERTY or null element.
	 */
	// Used by the emulator of the script recorder
	public String[] getKeyLayouts();

	/**
	 * Get list of keys
	 * 
	 */
	public String[] getRecordPhoneMode();

	/**
	 * Return a specific resource implement this method for all sampled
	 * measurements
	 * 
	 * @param ResourceName
	 *            Name of the resource
	 * @return specific resource value
	 * @throws PhoneException
	 * @throws PhoneException
	 */

	public HashMap<String, Long> getResources(List<String> sampledKeys) throws PhoneException;

	public int getType();

	public String getIncludeDir();
	public String getConfigFile();

	public boolean isDisabledPhone();

	//add robotium task
	public void sendCommandToExecuteToSolo(Object[] commands)throws PhoneException;

	public void setApkToTestWithRobotiumParam(String packName, String activityName, String packsourceDir, int versionCode)throws PhoneException;

	public ArrayList<String> getAllInstalledAPK() throws PhoneException;

	public ArrayList<String> getForegroundApp() throws PhoneException;
}