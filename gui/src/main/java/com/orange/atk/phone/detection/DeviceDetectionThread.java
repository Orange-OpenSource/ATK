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
 * File Name   : DeviceDetectionThread.java
 *
 * Created     : 19/02/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.detection;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneInterface;



public class DeviceDetectionThread extends Thread {
	
	private boolean pause = false;	
	private boolean exit = false;
	private static final int WAITING_TIME=3000;
	List<PhoneInterface> connectedPhones = new ArrayList<PhoneInterface>();

	public synchronized void resumeDetection() {
		if (pause) {
			pause = false;
			notify();
			Logger.getLogger(this.getClass() ).debug("RESUME connected devices detection ...");				
		}
	}
	
	public synchronized void pauseDetection() {
		if (!pause) {
			pause = true;
			Logger.getLogger(this.getClass() ).debug("PAUSE connected devices detection ...");				
		}
	}
	
	public void exit() {
		exit = true;
	}
	
	public void run() {
		Logger.getLogger(this.getClass() ).debug("START connected devices detection ...");	
		setName("checkPhone");
		while (!exit) {
			synchronized (this) {
				if (pause) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			AutomaticPhoneDetection.getInstance().checkDevices(); 
			try { Thread.sleep(WAITING_TIME); }
			catch (InterruptedException ie) { }
		}
		Logger.getLogger(this.getClass() ).debug("STOP connected devices detection ...");		
	}
	

}
