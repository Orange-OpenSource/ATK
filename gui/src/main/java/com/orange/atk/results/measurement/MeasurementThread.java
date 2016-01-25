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
 * File Name   : MeasurementThread.java
 *
 * Created     : 09/04/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.results.measurement;


import org.apache.log4j.Logger;

import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.manageListener.MyListener;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.results.logger.log.ResultLogger;




/**
 * This class is the thread which periodically measure values from battery,
 * memory, ..
 */

public class MeasurementThread implements Runnable {
	private int interval;
	Thread logThread = null;
	//boolean lauchIHM =false;
	MyListener listener=null;
	//PhoneInterface phoneInterface = null;
	// MeasureLists measureLists = null;
	private boolean isRunning = false;
	ResultLogger logger;

	/**
	 * Constructor
	 * @param l the logger which manages this thread
	 * @throws NullPointerException if l is null.
	 */
	public MeasurementThread(ResultLogger l) {
		if (l == null) {
			throw new NullPointerException(
			"A valid Logger should be provided");
		}
		logger = l;
	}

	/**
	 * Save memory, cpu, battery and storage used
	 * @throws PhoneException 
	 */
	public void logResourcesUsage() throws PhoneException {
		if(logger!=null&&listener!=null&&isRunning)
		{
			logger.addResourcesInfoToDocumentLogger();
		}
	}

	/**
	 * 
	 */
	public void run() {
		try {
			// loop until someone calls interrupt
			while (isRunning()) {
				try {
					logResourcesUsage();
				} catch (PhoneException e1) {
					ErrorManager.getInstance().addError(this.getClass().getName(), ResourceManager.getInstance().getString("RESOURCE_MEASUREMENT_ERROR"));
					setRunning(false);
				}	catch (Exception e2) {
					ErrorManager.getInstance().addError(this.getClass().getName(), ResourceManager.getInstance().getString("RESOURCE_MEASUREMENT_ERROR"), e2);
					setRunning(false);
				} 
				Thread.sleep(interval);
			}
			
			if(listener!=null)
				listener.removeMylistener();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass() ).debug("End of main logging");
		}
	}


	/**
	 * Start the main log of measurements
	 * 
	 * @param iInterval
	 *            interval between each measurement
	 * @throws IllegalArgumentException
	 *             if iInterval is equals to 0 or less
	 */
	public void start(int iInterval) {
		if (iInterval <= 0) {
			throw new IllegalArgumentException(iInterval + " <= 0 in Log.start");
		}
		interval = iInterval;
		logThread = new Thread(this);
		setRunning(true);

		listener=new MyListener( logger);
		listener.addMyListeners();

		logThread.setName("MeasurmentThread");  
		logThread.start();
	}

	/**
	 * Interrupt the log thread.
	 * 
	 * @throws NullPointerException
	 *             if start has not been successfully called before
	 */
	public void interrupt() {
		if (isRunning) {
			if (logThread != null) {
				try {
					setRunning(false);
					//logThread.interrupt();
	
				} catch (SecurityException ex) {
					Logger.getLogger(this.getClass()).
					warn("Internal error : Current Thread is not allow to interrupt the logThread in Logger.interrupt()");
				}
			} else {
				if(listener!=null)
					listener.removeMylistener();
				throw new NullPointerException(
				"Internal error : logThread is null in Logger.interrupt()");
			}
		}
	}

	/**
	 * Wait for log thread to be totally stopped. Logger.interrupt() should be
	 * called first.
	 * 
	 * @throws NullPointerException
	 *             if start has not been successfully executed before
	 */
	public void join() {
		try {
			if (logThread == null) {
				throw new NullPointerException(
				"Internal error : logThread is null in Logger.join()");
			}
			logThread.join();
			logThread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Indicate if the current log system is working.
	 * 
	 * @return true if the main log system is running, false otherwise.
	 */
	public boolean isAlive() {
		if (logThread == null) {
			return false;
		}
		return logThread.isAlive();
	}

	void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isRunning() {
		return isRunning;
	}
}
