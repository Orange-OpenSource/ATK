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
 * File Name   : DefaultPhone.java
 *
 * Created     : 28/01/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.orange.atk.interpreter.config.ConfigFile;
import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.manageListener.IPhoneKeyListener;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;

/**
 * This class simulates a phone, only for debug purpose.
 */

public class DefaultPhone implements PhoneInterface {
	Random rand = new Random();

	protected int cnxStatus = PhoneInterface.CNX_STATUS_DISCONNECTED;
	protected boolean isFailed = false;
	protected boolean isStarted = false;
	protected boolean isScriptRecording = false;

	private final static EventListenerList listeners = new EventListenerList();

	public DefaultPhone() {
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUID() {
		return "";
	}

	public DefaultPhone(ConfigFile configFile) {

	}

	public void beep() {
		Logger.getLogger(this.getClass()).debug("Beep generated !!!");
	}

	public long getResource(String ResourceName) throws PhoneException {

		return 0;
	}

	public HashMap<String, Long> getResources(List<String> sampledKeys) throws PhoneException {
		HashMap<String, Long> h = new HashMap<String, Long>();
		for (Iterator<String> iterator = sampledKeys.iterator(); iterator.hasNext();) {
			String resource = iterator.next();
			h.put(resource, getResource(resource));
		}
		return h;
	}

	public void fillStorage(long freeSpace) {
		Logger.getLogger(this.getClass()).debug(freeSpace + "o will be available now");
	}

	public void freeStorage() throws PhoneException {
		// TODO Auto-generated method stub
	}

	public String getCurrentMidlet() {
		// for preventing WaitWindow to loop
		if (rand.nextBoolean()) {
			return "NotDesktopExplorerWindow";
		} else {
			return "DesktopExplorerWindow";
		}
	}

	public void setPowerMonitor(boolean ispm) {

	}

	public long getStorageUsed() {
		return rand.nextInt();
	}

	public long getStorageUsed(String process) {
		return 0;
	}

	public void keyDown(String key) {
		Logger.getLogger(this.getClass()).debug("KeyDown : " + key);
	}

	public void keyPress(String key, int keyPressTime, int delay) {
		Logger.getLogger(this.getClass()).debug("KeyPress : " + key);
	}

	public void keyPress_AT(String key) throws PhoneException {
		Logger.getLogger(this.getClass()).debug("KeyPress : " + key);

	}

	public void keyUp(String key) {
		Logger.getLogger(this.getClass()).debug("KeyUp : " + key);
	}

	public void killMidlet(String process) {
		Logger.getLogger(this.getClass()).debug("Kill Window " + process);
	}

	public void registry(String base, String value, String string) {
		Logger.getLogger(this.getClass()).debug(
				"Registry [" + base + "," + value + "," + string + "]");
	}

	public void reset() {
		Logger.getLogger(this.getClass()).debug("Reset the phone");
	}

	public void runMidlet(String file) {
		Logger.getLogger(this.getClass()).debug("Running " + file);
	}

	public BufferedImage screenShot() {
		Logger.getLogger(this.getClass()).debug("Take screenshot");
		return null;
	}

	public void useCpu(int percentUse) {
		Logger.getLogger(this.getClass()).debug("Use CPU");
	}

	public void waitWindow(String process, int timeout) {
		Logger.getLogger(this.getClass()).debug(
				"Wait for window " + process + " during " + timeout + " s.");
	}

	public void setOrientation(int direction) {
		Logger.getLogger(this.getClass()).debug("Change orientation to " + direction);
	}

	public String getLastError() {
		return "No error";
	}

	public void startTestingMode() throws PhoneException {
		return;
	}

	public void setSleepMode(boolean issleep) {

	}

	public boolean startRandomTest(String HopperTest, String outputDir, ResultLogger mainLogger,
			Map<String, String> randomTestParam) {

		return true;

	}

	public boolean isMidletRunning(String MidletName) {

		return true;
	}

	public String getkeysAssociations(int key) {
		String cmd = "";
		return cmd;
	}
	public void stopTestingMode() {
		// Nothing to do

	}

	public PhoneInterface getInstance(Object... params) {
		return new DefaultPhone();
	}

	public void setFlightMode(boolean on) {
		if (on)
			Logger.getLogger(this.getClass()).debug("Set flight mode ON");
		else
			Logger.getLogger(this.getClass()).debug("Set flight mode OFF");
	}

	/**
	 * Send SMS
	 * 
	 * @param PhoneNumber
	 *            destination Phone Number
	 * @return Msg SMS Msg
	 * @throws PhoneException
	 */

	public void sendSMS(String PhoneNumber, String Msg) throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Send Msg");
	}

	public void disableUSBcharge() {

		Logger.getLogger(this.getClass()).debug("Diseable Battery charge");
	}

	public void sendEmail(String Subject, String Msg, String EmailDest, String NameDest,
			String NameSrc, String EmailSrc) {
		Logger.getLogger(this.getClass()).debug("Send fake mail Default Phone");

	}

	public void setPower(float power) {

	}

	public void fireStdOutput(String stdoutput) {
		for (IMeasureListener listener : getPerfListeners()) {
			listener.StdOutputChangee(stdoutput);

		}
	}

	public void fireFloatValue(float newMemValue, String key) {
		for (IMeasureListener listener : getPerfListeners()) {
			listener.FloatValueChangee(newMemValue, key);

		}

	}

	public void fireLongValue(long newMemValue, String key) {
		for (IMeasureListener listener : getPerfListeners()) {
			listener.LongValueChangee(newMemValue, key);

		}

	}

	public void addPerfListener(IMeasureListener listener) {
		listeners.add(IMeasureListener.class, listener);
	}

	public void removePerfListener(IMeasureListener listener) {
		listeners.remove(IMeasureListener.class, listener);
	}

	public IMeasureListener[] getPerfListeners() {
		return listeners.getListeners(IMeasureListener.class);
	}

	public void setvariable(String testFile, String outputDir) {
		// TODO Auto-generated method stub

	}

	public int getresult() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getphoneStatusMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getRandomTestList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getMonitorList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void stopRecordingMode() {
		// TODO Auto-generated method stub

	}
	public void phoneKeyPressed(String key) {
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.phoneKeyPressed(key);

		}

	}

	public void phoneKeyReleased(String key) {
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.phoneKeyReleased(key);

		}
	}

	public void addPhoneKeyListener(IPhoneKeyListener listener) {
		listeners.add(IPhoneKeyListener.class, listener);
	}

	public void removePhoneKeyListener(IPhoneKeyListener listener) {
		listeners.remove(IPhoneKeyListener.class, listener);
	}

	public IPhoneKeyListener[] getKeyListeners() {
		return listeners.getListeners(IPhoneKeyListener.class);
	}

	public String[] getScriptAssociations(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getRecordPhoneMode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getKeyLayouts() {
		// TODO Auto-generated method stub
		return null;
	}

	public void touchScreenDragnDrop(List<Position> path) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void touchScreenLongPress(Position click) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void touchScreenPress(Position click) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void touchScreenSlide(List<Position> path) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void mouseDown(int x, int y) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void mouseUp(int x, int y) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean failed) {
		isFailed = failed;
	}

	public int getCnxStatus() {
		return cnxStatus;
	}

	public void setCnxStatus(int status) {
		cnxStatus = status;
	}

	public void startRecordingMode() throws PhoneException {
		// TODO Auto-generated method stub
	}

	public boolean isInRecordingMode() {
		return isScriptRecording;
	}

	public boolean isInTestingMode() {
		return isStarted;
	}

	public boolean isDeviceRooted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void addTcpdumpLineListener(TcpdumpLineListener listener) {
		// TODO Auto-generated method stub

	}

	public int getType() {
		return PhoneInterface.TYPE_DEFAULT;
	}

	public String getIncludeDir() {
		return null;
	}

	public String getConfigFile() {
		return null;
	}

	@Override
	public boolean isDisabledPhone() {
		return false;
	}

	@Override
	public void sendCommandToExecuteToSolo(Object[] commands) throws PhoneException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setApkToTestWithRobotiumParam(String packName,
			String activityName, String packsourceDir, int versionCode)
					throws PhoneException {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getAllInstalledAPK() throws PhoneException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getForegroundApp() throws PhoneException {
		// TODO Auto-generated method stub
		return null;
	}


}
