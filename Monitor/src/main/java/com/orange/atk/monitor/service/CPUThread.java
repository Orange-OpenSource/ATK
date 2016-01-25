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
 * File Name   : CPUThread.java
 *
 * Created     : 17/02/2010
 * Author(s)   : Laurent Gottely
 */
package com.orange.atk.monitor.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

public class CPUThread implements Runnable {


	private static final String TAG = "CPUThread";
	private long currentLoad;
	private long cpufrequency;
	private long last_total_cpu= -1;
	private long last_used_cpu = -1;
	//	private long last_time = -1;
	private long time = -1;
	private long total_cpu = -1;
	private long used_cpu = -1;
	private long _frequency= 1000;
	private final static int REFRESH_MAX_FREQ = 60000;
	private boolean _stop = false;
	private LinkedList<ProcessInformation> listprocess; 

	public CPUThread(long frequency) {
		listprocess = new LinkedList<ProcessInformation>();
		_frequency = frequency;
		_stop = false;
	}
	public void quit() {
		_stop =true;
	}
	public LinkedList<ProcessInformation> getListprocess() {
		return listprocess;
	}
	public void Addprocesstolist(ProcessInformation processinformation) {
		listprocess.add(processinformation);
		return;
	}
	public void ClearProcessList() {
		if(null != listprocess)
			listprocess.clear();
		return;
	}

	public void run() {
		int i_refresh = 0;
		int max_i_refresh = (int) (REFRESH_MAX_FREQ / _frequency);
		String maxCpuFrequency = "0";
		while (!_stop) {
			try {			

				FileReader fileReader = new FileReader("/proc/stat");
				BufferedReader in = new BufferedReader( fileReader,20);			
				time = System.currentTimeMillis();
				String ret = in.readLine();
				fileReader.close();

				String [] s = ret.split(" +");
				int [] v = new int[5];
				if (s.length < 6) {throw new Exception("BAD CPU INFO");}

				v[0]=0;

				for (int i = 1; i < 5; i++) {
					v[i]=Integer.parseInt(s[i]);	
				}

				total_cpu = v[1]+v[2]+v[3]+v[4];
				used_cpu = v[1]+v[2]+v[3];
				//currentLoad =(int) ((used_cpu - last_used_cpu)*1000 / (time - last_time));
				currentLoad =(int) ((used_cpu - last_used_cpu)*100 / (total_cpu - last_total_cpu));

				/*
				fileReader = new FileReader("/proc/cpuinfo");
				in = new BufferedReader( fileReader,50);			

				boolean bogoFound = false;
	
				ret = in.readLine();
				while (!bogoFound && ret != null) {
					s = ret.split("[( +),.]");
					if (s.length>1 && s[0].toLowerCase().startsWith("bogomips")) bogoFound = true;
					ret = in.readLine();
				}
				// We round to the integer
				if (bogoFound) cpufrequency = s[1];
				else cpufrequency="0";
				fileReader.close();*/
				
				fileReader = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
				in = new BufferedReader( fileReader,50);			
				String curCpuFrequency = in.readLine();
				fileReader.close();
				//Log.v(TAG,"scaling_cur_freq = "+curCpuFrequency);
				
				if (i_refresh==0) {
					fileReader = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
					in = new BufferedReader( fileReader,50);			
					maxCpuFrequency = in.readLine();
					fileReader.close();
					//Log.v(TAG,"cpuinfo_max_freq = "+maxCpuFrequency);
				}
				i_refresh++;
				if (i_refresh>max_i_refresh) i_refresh=0;
				
				cpufrequency = (long) (Double.parseDouble(curCpuFrequency) * 100.0 / Double.parseDouble(maxCpuFrequency) );
				//Log.v(TAG,"currentLoad = "+currentLoad+" % - Frequency: "+cpufrequency+" %");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(null != listprocess)
			{
				for(int i = 0;i < listprocess.size();i++){
					ProcessInformation processinformation = listprocess.get(i);
					if(-1 != processinformation.getPID()){
						try {
							FileReader fileReader = new FileReader("/proc/"+processinformation.getPID()+"/stat");
							BufferedReader in = new BufferedReader( fileReader,50);			

							String ret = in.readLine();
							fileReader.close();

							String [] s = ret.split(" +");				

							long used_cpu_process =  0;
							for (int j = 0; j < 4; j++) {
								used_cpu_process+=Long.parseLong(s[j+13]);	
							}
							int processload = (int) (((used_cpu_process-processinformation.getLast_used_cpu_process())*100)
							/(total_cpu - last_total_cpu));
							//We ignore the first value after initialization
							if(processinformation.getLast_used_cpu_process()==0)
								processload = 0;
							if(processload>100){
								Log.v(TAG,"Process load > 100. Set to 100.");
								processload = 100;
							}
							if(processload<0){
								Log.v(TAG,"Process load < 0. Set to 0.");
								processload = 0;

							}
							processload = (int)((long) processload * this.cpufrequency / 100.0);
							processinformation.setCpu_load(processload);
							processinformation.setLast_used_cpu_process(used_cpu_process);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.v(TAG,"The process is probably not running. Reset the information.");
							processinformation.reset();
							e.printStackTrace();
						}

					}
					else
						processinformation.reset();
				}
			}
			last_total_cpu = total_cpu;
			last_used_cpu = used_cpu;
			try {
				Thread.sleep(_frequency);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

	}
	public long getCPU() {
		return currentLoad;
	}	
	public long getCPUfrequency() {
		return cpufrequency;
	}	

}
