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
 * File Name   : IProgressMonitor.java
 *
 * Created     : 05/06/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli;

/**
 * This interface holds the necessary operations for a task gives informations
 * to a monitor about it's completness, and ask this monitor if it should stop
 * (because it has been canceled for example)
 * @author moteauni
 */
public interface IProgressMonitor {

	/**
	 * Increments the completeness counter
	 */
	public void increment();
	public void increment(String message);
	public void setMessage(String message);

	/**
	 * Check if someone decide to stop the task
	 * @return true if the task have to be stopped as soon as possible, false otherwise.
	 */
	public boolean isStop();

	/**
	 * Indicate that the task should stop as soon as possible.
	 */
	public void setStop();

}
