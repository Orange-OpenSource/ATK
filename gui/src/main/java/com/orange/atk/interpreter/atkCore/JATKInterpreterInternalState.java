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
 * File Name   : JATKInterpreterInternalState.java
 *
 * Created     : 27/11/2008
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.interpreter.atkCore;


import javax.swing.event.EventListenerList;

import com.orange.atk.manageListener.IMeasureListener;


/**
 * This class represents internal states of the interpreter. You should not call
 * set* functions which are called by {@link JATKInterpreter} and
 * {@link ActionToExecute} classes to store the interpreter state.
 */

public class JATKInterpreterInternalState {
	// boolean which indicates if StartMainLog has been Called
	private boolean isStartMainLogCalled = false;
	// current value of a loop (-1 if we are not in a loop)
	private int loopValue = -1;
	// indicate the current parsed file (include file name, main file name, ...)
	private String currentScript = null;
	// directory for results files
	private String logDir = null;

    private final static EventListenerList listeners = new EventListenerList();

	/**
	 * constructor getLoopValue() == -1 isStartMainLogCalled() == false
	 * getIncludeDir() == null getLogDir() == null getCurrentScript()==null
	 */
	public JATKInterpreterInternalState() {
	}

	/**
	 * Returns the current executed script
	 * 
	 * @return path of the current executed script
	 */
	public String getCurrentScript() {
		return currentScript;
	}

	/**
	 * Do not use this function.
	 * 
	 * @param currentScript
	 * @ensure this.getCurrentScript().equals(currentScript) == true
	 */
	public void setCurrentScript(String currentScript) {
		this.currentScript = currentScript;
	}

	/**
	 * Indicate if the periodic log system has started
	 * 
	 * @return true if the log system has started its periodic measurements of
	 *         phone statistics, false otherwise.
	 */
	public boolean isStartMainLogCalled() {
		return isStartMainLogCalled;
	}

	/**
	 * Do not use this function, only used by {@link ActionToExecute} class.
	 * 
	 * @param isStartMainLogCalled
	 * @ensure this.isStartMainLogCalled() == isStartMainLogCalled
	 */
	public void setStartMainLogCalled(boolean isStartMainLogCalled) {
		this.isStartMainLogCalled = isStartMainLogCalled;
	}

	/**
	 * This function returns the current indice of the loop. It allows the
	 * interpreter to be able to treat overlapped loops.
	 * 
	 * @return the current indice of the loop, -1 if we are not currently in a
	 *         loop
	 */
	public int getLoopValue() {
		
		return loopValue;
	}

	/**
	 * Do not use this function, only used by {@link JATKInterpreter} class.
	 * 
	 * @param loopValue
	 * @ensure this.getLoopValue() == loopValue
	 */
	public void setLoopValue(int loopValue) {
		if(this.loopValue!=loopValue){
			fireLoopChanged(String.valueOf(loopValue));

		}

		this.loopValue = loopValue;

	
	}

	/**
	 * Return the folder where pictures, graphs, reports, ... will be saved
	 * 
	 * @return path to log folder
	 */
	public String getLogDir() {
		return logDir;
	}

	/**
	 * You can change the folder where files will be saved
	 * 
	 * @param logDir
	 *            path to the new folder. Think to provide a valid folder (
	 *            read/write rights, existing folder...)
	 * @ensure this.getLogDir().equals(logDir) == true
	 */
	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	
	 protected void fireLoopChanged(String newMemValue) {
         for(IMeasureListener listener : getPerfListeners()) {
             listener.addLoopChangee( newMemValue);
         
    
     }
 }	  
	 
	 
	  public static  void addPerfListener(IMeasureListener listener) {
	        listeners.add(IMeasureListener.class, listener);
	    }
	    
	    public void removePerfListener(IMeasureListener listener) {
	        listeners.remove(IMeasureListener.class, listener);
	    }
	 
	 
	 
	  public IMeasureListener[] getPerfListeners() {
	        return listeners.getListeners(IMeasureListener.class);
	    }
	
}
