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
 * File Name   : RecordPhoneEventListener.java
 *
 * Created     : 11/12/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.manageListener;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.scriptRecorder.ScriptController;
import com.orange.atk.util.Position;


//TODO : need to thread everything ?
public class RecordPhoneEventListener implements IPhoneKeyListener {
	private long startPress= 0;
	private Date stopRecordTime;
	protected ScriptController controller;
	protected Date startRecordTime;
	private String lastKeyPressed;

	
	public RecordPhoneEventListener() {
		controller = ScriptController.getScriptController();
		startRecordTime = new Date();
	}
	
	public void beep() {
		actionwithsleep("Beep",0);
	}

	public void disableUSBcharge() {
		action("DisableUSBcharge");
		
	}

	public void fillStorage(final long fillSpace) {
		action("FillStorage("+fillSpace+")");	
	}
	
	public void freeStorage() {
		action("FreeStorage()");	
	}

	public void include(final String include) {
		action("Include("+include+")");	
	}

	public void keyPress(final String key, final int keyPressTime, final int delay) {
		actionwithsleep("Key('"+key+"', "+keyPressTime+", "+delay+")", keyPressTime);
	}

	public void killMidlet(final String midlet) {
		action("KillMidlet("+midlet+")");	
	}

	public void log(final String comment) {
		action("Log("+comment+")");
	}


	public void phoneKeyPressed(final String key) {
		Logger.getLogger(this.getClass() ).debug("phoneKeyPressed "+key);
		if (lastKeyPressed != null) action("KeyDown('"+lastKeyPressed+"')");
		sleep();
		lastKeyPressed = key;
		startPress= System.currentTimeMillis();
	}

	public void phoneKeyReleased(final String key) {
		Logger.getLogger(this.getClass() ).debug("phoneKeyReleased "+key);
		if (key.equals(lastKeyPressed)) {
			if (startPress!=0) {
				long keyPressTime = System.currentTimeMillis() - startPress;
				action("Key('"+key+"', "+keyPressTime+", "+0+")");
				startRecordTime = new Date();
			}
			lastKeyPressed = null;
		} else {
			actionwithsleep("KeyUp('"+key+"')",0);
		}
	}

	public void reset() {
		action("Reset");
	}

	public void runMidlet(final String midlet) {
		action("RunMidlet("+midlet+")");	
	}

	public void screenshot() {
		action("Screenshot");	
	}

	public void screenshot(final String comment) {
		action("Screenshot("+comment+")");
	}

	public void sendEmail(final String subject, final String msg, final String emailDest,
			final String nameDest, final String nameSrc, final String emailSrc) {
		action("SendEmail("+subject+", "+msg+", "+emailDest+
				", "+nameDest+", "+nameSrc+", +"+emailSrc+")");
	}

	public void sendSMS(final String phoneNumber, final String msg) {
			action("SendSMS("+phoneNumber+", "+msg+")");
	}

	public void setFlightMode(final boolean on) {
		action("SetFlightMode("+on+")");
	}

	public void setOrientation(final int direction) {
		action("SetOrientation("+direction+")");
	}

	public void sleep(final int time) {
		action("Sleep("+time+")");
	}


	public void startMainLog(final int defaultTime) {
		action("StartMainLog("+defaultTime+")");
	}

	public void stopMainLog() {
		action("StopMainLog");
	}

	public void stopOnKey(final int key) {
		action("StopOnKey("+key+")");
	}

	public void useCpu(final int percentUse) {
		action("UseCpu("+percentUse+")");
	}

	public void waitWindow() {
		action("WaitWindow");
	}

	public void waitWindow(final String process, final int timeout) {
		action("WaitWindow("+process+", "+timeout+")");
	}

	public void touchScreenDragnDrop(List<Position> path) {
		if (lastKeyPressed != null) {
			action("KeyDown('"+lastKeyPressed+"')");
			lastKeyPressed = null;
		}
		String action = "TouchScreenDragnDrop(";
		for(int i=0; i<(path.size()-1) ; i++)
			action += path.get(i).toString()+", ";
		
		action+=path.get(path.size()-1).toString()+" )";
		actionwithsleep(action, path.get(path.size()-1).getTime());
	}

	public void touchScreenPressed(Position click) {
		if (lastKeyPressed != null) {
			action("KeyDown('"+lastKeyPressed+"')");
			lastKeyPressed = null;
		}
		actionwithsleep("TouchScreenPress("+click.getX()+", "+click.getY()+","+click.getTime()+")",0);			
		
	}

	public void touchScreenSlide(List<Position> path) {
		if (lastKeyPressed != null) {
			action("KeyDown('"+lastKeyPressed+"')");
			lastKeyPressed = null;
		}		
		String action = "TouchScreenSlide(";
		for(int i=0; i<(path.size()-1) ; i++)
			action += path.get(i).toString()+", ";
		
		action+=path.get(path.size()-1).toString()+") ";
		actionwithsleep(action, path.get(path.size()-1).getTime());
		
	}

	private void actionwithsleep(final String action, final long actionDuration) {
		stopRecordTime=new Date();
		new Runnable() {
			public void run() {
				//Add sleep
				if(stopRecordTime.getTime()-startRecordTime.getTime()- actionDuration>0)
				controller.addEvent("Sleep("+(stopRecordTime.getTime()-startRecordTime.getTime()- actionDuration)+ ")");
				controller.addEvent(action);
			}
		}.run();
		startRecordTime = new Date();
	}

	private void action(final String action) {
		new Runnable() {
			public void run() {
				controller.addEvent(action);
			}
		}.run();
	}
	
	private void sleep(){
		stopRecordTime=new Date();
		new Runnable() {
			public void run() {
				//Add sleep
				controller.addEvent("Sleep("+(stopRecordTime.getTime()-startRecordTime.getTime())+ ")");
			}
		}.run();
		startRecordTime = new Date();
	}

	public Date getStartRecordTime() {
		return startRecordTime;
	}

	public void setStartRecordTime(Date startRecordTime) {
		this.startRecordTime = startRecordTime;
	}
	
}
