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
 * File Name   : PhoneRecorder.java
 *
 * Created     : 15/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.manageListener.IPhoneKeyListener;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;

public class PhoneRecorder implements PhoneInterface {

	private final static EventListenerList listeners = new EventListenerList();
	protected int cnxStatus = PhoneInterface.CNX_STATUS_DISCONNECTED;
	private boolean isFailed = false;
	private boolean isStarted = false;
	private boolean isScriptRecording = false;

	public void beep() throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.beep();
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUID() {
		return "";
	}

	public void disableUSBcharge() throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.disableUSBcharge();

		}
	}

	public void fillStorage(long fillSpace) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.fillStorage(fillSpace);
		}
	}

	public void freeStorage() throws PhoneException {
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.freeStorage();
		}

	}

	public IPhoneKeyListener[] getKeyListeners() {
		return listeners.getListeners(IPhoneKeyListener.class);
	}

	public void keyPress(String key, int keyPressTime, int delay) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.keyPress(key, keyPressTime, delay);

		}
	}

	public void killMidlet(String midlet) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.killMidlet(midlet);

		}
	}

	public boolean startRandomTest(String HopperTest, String outputDir, ResultLogger mainLogger,
			Map<String, String> randomTestParam) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removePhoneKeyListener(IPhoneKeyListener listener) {
		listeners.remove(IPhoneKeyListener.class, listener);

	}

	public void reset() throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.reset();

		}
	}

	public void runMidlet(String midlet) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.runMidlet(midlet);

		}
	}

	public BufferedImage screenShot() throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.screenshot();

		}
		return null;
	}

	public void screenShot(String comment) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.screenshot(comment);

		}
	}

	public void sendEmail(String Subject, String Msg, String EmailDest, String NameDest,
			String NameSrc, String EmailSrc) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.sendEmail(Subject, Msg, EmailDest, NameDest, NameSrc, EmailSrc);

		}
	}

	public void sendSMS(String PhoneNumber, String Msg) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.sendSMS(PhoneNumber, Msg);
		}
	}

	public void setFlightMode(boolean on) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.setFlightMode(on);
		}
	}

	public void setOrientation(int direction) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.setOrientation(direction);
		}

	}

	public void startTestingMode() throws PhoneException {
		return;
	}

	public void useCpu(int percentUse) throws PhoneException {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.useCpu(percentUse);
		}
	}

	public void sleep(int time) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.sleep(time);
		}
	}

	public void startMainLog(int defaultTime) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.startMainLog(defaultTime);
		}
	}

	public void stopMainLog() {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.stopMainLog();
		}
	}

	public void stopOnKey(int key) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.stopOnKey(key);
		}
	}

	public void waitWindow(String process, int timeout) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.waitWindow(process, timeout);
		}
	}

	public void waitWindow() {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.waitWindow();
		}
	}

	public void log(String comment) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.log(comment);
		}

	}

	public void include(String include) {
		// IPhoneKeyListener[] s = getKeyListeners();
		for (IPhoneKeyListener listener : getKeyListeners()) {
			listener.include(include);
		}
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

	public void addPerfListener(IMeasureListener listener) {
		// TODO Auto-generated method stub

	}

	public void addPhoneKeyListener(IPhoneKeyListener listener) {
		// TODO Auto-generated method stub

	}

	public boolean checkcnx() {
		// TODO Auto-generated method stub
		return false;
	}

	public void fireFloatValue(float newMemValue, String key) {
		// TODO Auto-generated method stub

	}

	public String getCurrentMidlet() throws PhoneException {
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

	public String[] getKeyLayouts() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMeasureListener[] getPerfListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getRecordPhoneMode() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getResource(String ResourceName) throws PhoneException {
		// TODO Auto-generated method stub
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

	public String[] getScriptAssociations(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isMidletRunning(String MidletName) throws PhoneException {
		// TODO Auto-generated method stub
		return false;
	}

	public void keyDown(String key) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void keyUp(String key) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void fireLongValue(long newValue, String key) {
		// TODO Auto-generated method stub

	}

	public void removePerfListener(IMeasureListener listener) {
		// TODO Auto-generated method stub

	}

	public void setConnected(boolean cnxfail) {
		// TODO Auto-generated method stub

	}

	public void setSleepMode(boolean issleep) {
		// TODO Auto-generated method stub

	}

	public void setvariable(String testFile, String outputDir) {
		// TODO Auto-generated method stub

	}

	public void fireStdOutput(String Stdoutput) {
		// TODO Auto-generated method stub

	}

	public void stopTestingMode() {
		// TODO Auto-generated method stub

	}

	public void stopExecution() throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void stopRecordingMode() {
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

	public void setPowerMonitor(boolean ispm) {
		// TODO Auto-generated method stub

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
		return PhoneInterface.TYPE_RECORDER;
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
