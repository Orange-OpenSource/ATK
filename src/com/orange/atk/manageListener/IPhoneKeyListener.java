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
 * File Name   : IPhoneKeyListener.java
 *
 * Created     : 15/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.manageListener;

import java.util.EventListener;
import java.util.List;

import com.orange.atk.util.Position;


public interface IPhoneKeyListener extends EventListener{
	//Low level phone key  event
	void phoneKeyPressed(String key);
	void phoneKeyReleased(String key);

	//higher level key event
	void keyPress(String key, int keyPressTime, int delay);
	
	//highLevel TouchScreen event
	void touchScreenPressed(Position click);
	void touchScreenSlide( List<Position> path);
	void touchScreenDragnDrop( List<Position> path);
	
	//other phone event
	void beep();
	void disableUSBcharge();
	void runMidlet(String midlet);
	void screenshot();
	void sendEmail(String subject, String msg, String emailDest,
			String nameDest, String nameSrc, String emailSrc);
	void sendSMS(String phoneNumber, String msg);
	void sleep(int time);
	
	//logger event
	void startMainLog(int defaultTime);
	void stopMainLog();
	
	void useCpu(int percentUse);
	void stopOnKey(int key);
	void waitWindow();
	void waitWindow(String process, int timeout);
	void setOrientation(int direction);
	void setFlightMode(boolean on);
	void reset();
	void log(String comment);
	void killMidlet(String midlet);
	void include(String include);
	void fillStorage(long fillSpace);
	void freeStorage();

	void screenshot(String comment);
	
}
