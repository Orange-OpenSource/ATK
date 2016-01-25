/*
 * Software Name : ATK
 *
 * Copyright (C) 2007 - 2012 France T�l�com
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
 * File Name   : ProcessInformation.java
 *
 * Created     : 17/02/2010
 * Author(s)   : Laurent Gottely
 */
package com.orange.atk.monitor.service;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ProcessInformation {
	private String process_name;  
	private int PID;

	private int cpu_load;
	private long last_used_cpu_process;
	
		
	public ProcessInformation(String processName) {
		super();
		process_name = processName;
		PID = -1;
		last_used_cpu_process = 0;
		cpu_load = 0;
		
	}

	public boolean equals(String string){
		return string.equals(process_name);
	}

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String processName) {
		process_name = processName;
	}

	public boolean isRunning(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService("activity");
		List<ActivityManager.RunningAppProcessInfo> raps = am.getRunningAppProcesses();
		for (int i=0; i< raps.size(); i++) {
			ActivityManager.RunningAppProcessInfo rap = raps.get(i);
			if (rap.processName.equals(process_name)) return true;
			/*for (int j=0; j<rap.pkgList.length; j++) {
				Log.v(TAG,"   pkg =>"+rap.pkgList[j]);
			}*/
		}
		return false;
	}
	
	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public int getCpu_load() {
		return cpu_load;
	}

	public void setCpu_load(int cpuLoad) {
		cpu_load = cpuLoad;
	}

	public long getLast_used_cpu_process() {
		return last_used_cpu_process;
	}

	public void setLast_used_cpu_process(long lastUsedCpuProcess) {
		last_used_cpu_process = lastUsedCpuProcess;
	}
	
	public void reset(){
		setPID(-1);
		setCpu_load(0);
		setLast_used_cpu_process(0);
	}
	
}
