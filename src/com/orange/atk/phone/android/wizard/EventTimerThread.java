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
 * File Name   : EventTimerThread.java
 *
 * Created     : 31/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import org.apache.log4j.Logger;

public class EventTimerThread extends Thread {
	private boolean pause = true;	
	private boolean running = true;
	private int timeout = 300; // ms
	private long delayTime = 0;
	private TimeOutListener listener;
	
	public EventTimerThread(TimeOutListener listener, int timeout) {
		this.listener = listener;
		this.timeout = timeout;
	}
	
	public void run() {
		while(running) {
			synchronized (this) {
				if (pause) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (delayTime !=0){
				if (System.currentTimeMillis() < delayTime) {
					try {
						Thread.sleep(delayTime - System.currentTimeMillis());
					} catch (InterruptedException e) {}
				} else {
					listener.notifyTimeOut();
					delayTime = 0;
					pauseTimer();
				}
			}
			
		}
	}
	
	private synchronized void resumeTimer() {
		if (pause) {
			pause = false;
			notify();
		}
	}
	
	private synchronized void pauseTimer() {
		if (!pause) {
			pause = true;
		}
	}

	public void newEventTime(long time) {
		delayTime = time + timeout;
		resumeTimer();
	}
	
	public void stopRunning() {
		Logger.getLogger(this.getClass()).debug("STOP RUNNING TIMER");
		resumeTimer();
		running = false;
	}


}
