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
 * File Name   : UITask.java
 *
 * Created     : 27/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui.tasks;

import com.orange.atk.atkUI.corecli.IProgressMonitor;


/**
 * This class represents a task that is launched from a GUI, and should not freeze it.
 * Concrete tasks behaviour has to be set in the run() operation.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class UITask implements Runnable, IProgressMonitor {

	/** A flag indicating the need to stop the task. */
	private boolean shouldStop = false;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

	/* (non-Javadoc)
	 * @see com.francetelecom.rd.matos.coregui.IProgressMonitor#increment()
	 */
	public void increment() {
		increment(null);
	}

	/**
	 * Tests the stop flag.
	 * @return true if tha task have to stop as soon as possible, false otherwise.
	 */
	public boolean isStop() {
		return shouldStop;
	}

	/**
	 * Raises a flag indicating the task should stop as soon as possible.
	 */
	public void setStop() {
		shouldStop = true;
	}

}
