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
 * File Name   : Process.java
 *
 * Created     : 28/01/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.system;

/**
 * Native class to get informations about usage of CPU by a process
 */

public class Process {
	static {
		System.loadLibrary("sys_process");
	}

	/**
	 * Native function to get the pid of from a process name
	 * 
	 * @param processName
	 *            name of the process
	 * @return the pid of the associated process
	 */
	public native static int getPIDFromProcessName(String processName);

	/**
	 * Native function to get the CPU usage of a griven process
	 * 
	 * @param pid
	 *            pid of the process
	 * @return the pourcentage of CPU used by the process during the last
	 *         second.
	 */
	public native static int getCPUProcess(int pid);

	/**
	 * Native function to get the real time accumulated by process
	 * 
	 * @param pid
	 *            pid of the process
	 * @return real time accumulated by process
	 */
	public native static int getCurrentRTime(int pid);
}
