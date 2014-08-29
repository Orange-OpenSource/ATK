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
 * File Name   : ATKMonitorService.java
 *
 * Created     : 17/02/2010
 * Author(s)   : Laurent Gottely
 */
package com.orange.atk.monitor.service;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.orange.atk.monitor.IATKMonitorCom;
import com.orange.atk.monitor.IATKMonitorEventListener;
import com.orange.atk.monitor.R;

public class ATKMonitorService extends ATKService implements Runnable{
	private static final String TAG = "ATKMonitorService";
	private final static int PORT = 1357;
	private static final int ATK_MONITOR = 0;
	private static int count = 0;

	private CPUThread cput;

	protected List <IATKMonitorEventListener> listeners = new ArrayList<IATKMonitorEventListener>();
	private String totalmem = "0"; 
	private int batteryLevel = 0;
	private StatFs internalStorage;
	private StatFs externalStorage;
	private long totalRxBytesOffset = -1;
	private long totalTxBytesOffset = -1;
	private HashMap<Integer, Long> uidRxBytesOffsets = new HashMap<Integer, Long>();
	private HashMap<Integer, Long> uidTxBytesOffsets = new HashMap<Integer, Long>();

	private final IATKMonitorCom.Stub mBinder = new IATKMonitorCom.Stub () {

		public String getCPU() throws RemoteException {
			return getCpu();
		}

		public String getConnection() throws RemoteException {
			return getConnection();
		}

		public String getMem() throws RemoteException {
			return getMem();
		}
		public void addEventListener(IATKMonitorEventListener listener)
		throws RemoteException {
			listeners.add(listener);
		}
		public void removeAllEventListeners() throws RemoteException {
			listeners.clear();
		}
		public void removeEventListener(IATKMonitorEventListener listener)
		throws RemoteException {
			listeners.remove(listener);	
		}

		public void stop() throws RemoteException {
			quit();
			return;

		}

	};

	public  void addNotification(boolean state, String label) {
		// Create notification
		PendingIntent contentIntent = PendingIntent.getActivity(_context, 0, 
				new Intent("com.orange.atk.monitor.CLIENT"), Intent.FLAG_ACTIVITY_NEW_TASK);
		int iconId; 
		if (state) {
			iconId  = R.drawable.icon_monitor_on;
		} else {
			iconId  = R.drawable.icon_monitor_off; 
		}
		Notification notif = new Notification(iconId, "", System.currentTimeMillis());

		notif.flags = Notification.FLAG_NO_CLEAR;
		notif.setLatestEventInfo( _context , "ATKMonitorService",label, contentIntent);

		// Send notification
		NotificationManager notificationManager = (NotificationManager)_context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ATK_MONITOR, notif);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG,"onBind");
		onStart(intent,0);
		return mBinder;
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		PendingIntent contentIntent = PendingIntent.getActivity(_context, 0, 
				new Intent("com.orange.atk.monitor.CLIENT"), Intent.FLAG_ACTIVITY_NEW_TASK);
		int iconId  = R.drawable.icon_monitor_on;
		
		Notification notif = new Notification(iconId, "", System.currentTimeMillis());

		notif.flags = Notification.FLAG_NO_CLEAR;
		notif.setLatestEventInfo( _context , "ATKMonitorService","Monitor started", contentIntent);

		// Send notification
		NotificationManager notificationManager = (NotificationManager)_context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ATK_MONITOR, notif);

		// Internal Storage init
		String dataDir = Environment.getDataDirectory().getPath();
		internalStorage = new StatFs(dataDir);
		Log.v(TAG,"Internal data directory = "+dataDir);
		// External Storage init
		dataDir = Environment.getExternalStorageDirectory().getPath();
		externalStorage = new StatFs(dataDir);
		Log.v(TAG,"External data directory = "+dataDir);
		this.batteryLevel();
		//temp_testings();
		if (t==null) {
			Thread t2 = new Thread(this);
			t2.setName("ATKMonitorServiceThread");
			t2.start();
		}

	}

	
	private void notifyGlobalChange(String s, String totalmem){
		IATKMonitorEventListener listener;
		int i; 
		for (i=0; i < listeners.size();i++) {
			listener = (IATKMonitorEventListener)listeners.get(i);
			try {
				listener.globalChanged(s,totalmem);
			} catch (RemoteException e) {				
				e.printStackTrace();
			}
		}
	}

	protected void quit() {
		super.quit();
		if(cput != null) cput.quit();
		addNotification(false,"Monitor Stopped");
	}

	protected String analyseInput(String inputline) {
		if( (++count%60)==0){
			System.gc();
			addNotification(true,"Monitor running");
			Log.d(TAG, "update notification #"+count);
		}
		if (inputline.equals("VERSION")) {
			Log.v(TAG,"VERSION");
			//Get the version
			PackageInfo pi = null;
			try {
				pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				Log.v(TAG, "Error : No version name in manifest");
			}
			if (pi !=null) return pi.versionName;
			else return "0";
		} else if (inputline.matches("INIT.+")) {
			Log.v(TAG,"INIT");
			// reportCpu();
			String [] listnameprocess = inputline.split(" +");		
			ProcessInformation processinformation;

			//by default, remove every process from the list
			cput.ClearProcessList();

			//Parse the init command and add new process information in the list
			for(int i=1; i<listnameprocess.length; i++){
				processinformation = new ProcessInformation(listnameprocess[i]);
				cput.Addprocesstolist(processinformation);

				int pid = getPidFromName(listnameprocess[i]);
				if(-1 != pid)
					processinformation.setPID(pid);
			}
			if (Integer.valueOf(Build.VERSION.SDK) >= 8) {
				totalRxBytesOffset = getTotalRxBytes();
				totalTxBytesOffset = getTotalTxBytes();
//				Log.v("TEST", "RxOffset="+totalRxBytesOffset+", TxOffset="+totalTxBytesOffset);
			}else {
				Log.v(TAG,"API level < 8 : No network data ");
			}
			
			return "Init Ok ";
		} else if (inputline.matches("RES")) {		
			// Return resource information formatted as follow:
			// GLOBAL CPU_TOTAL PROCESSOR_SPEED MEM_USED STORAGE_DATA_USED BATTERY_LEVEL
			// Optional:
			// NAME_PROCESS CPU_USED MEM_VIRTUAL
			// ...

			//Log.v(TAG,"getCPU");
			// reportCpu();
			LinkedList<ProcessInformation> listprocess = cput.getListprocess();
			ProcessInformation processinformation;
			String ressource = "GLOBAL ";
			String cpuLoad = String.valueOf((int) (cput.getCPU() * cput.getCPUfrequency() / 100.0));
			ressource+= cpuLoad + " ";
			ressource+= getTotalMemoryUsed();

			//Get the state of the internal and external storage for data
			internalStorage.restat(Environment.getDataDirectory().getPath());
			externalStorage.restat(Environment.getExternalStorageDirectory().getPath()); // Sdcard
			long freeInData = (long)(internalStorage.getBlockCount()-internalStorage.getAvailableBlocks())*internalStorage.getBlockSize();
			long freeExData = (long)(externalStorage.getBlockCount()-externalStorage.getAvailableBlocks())*externalStorage.getBlockSize();
			//Log.v(TAG,"int blockCount "+internalStorage.getBlockCount()+" availableBlack "+internalStorage.getAvailableBlocks()+" blackSize "+internalStorage.getBlockSize());
			ressource+=" "+String.valueOf(freeInData/1024L);
			if (externalStorage.getBlockCount()==0) ressource+=" -1";
			else ressource+=" "+String.valueOf(freeExData/1024L);
			ressource+=" "+batteryLevel; // %
			
			long rX = -1000,tX = -1000 ;
			if (Integer.valueOf(Build.VERSION.SDK) >= 8) {
				long newTotalRx = getTotalRxBytes();
				long newTotalTx = getTotalTxBytes();
				
				rX = newTotalRx - totalRxBytesOffset;
				if (rX < 0) rX = 0;
				tX = newTotalTx - totalTxBytesOffset;
				if (tX < 0) tX = 0;
				
				totalRxBytesOffset = newTotalRx;
				totalTxBytesOffset = newTotalTx;
			}
			ressource += " " + rX; // %
			ressource += " " + tX; // %
			
			//Get info for the processes
			if(null != listprocess){
				for(int i = 0;i < listprocess.size();i++){
					int pid = -1;
					processinformation = listprocess.get(i);
					if (processinformation.isRunning(this._context)) {
						String nameprocess = processinformation.getProcess_name(); 
						pid = processinformation.getPID();
						if(-1 == pid){
							pid = getPidFromName(nameprocess);
	
							// Test if the process is running
							if(-1 == pid){
								// The process is not running, we set default value
								processinformation.setCpu_load(0);
								processinformation.setLast_used_cpu_process(0);
							}
							else{
								processinformation.setPID(pid);
							}
						}
						int loadprocess = processinformation.getCpu_load();
						ressource+="\n"+nameprocess+" "+String.valueOf(loadprocess);
						// Memory
						if(-1 != pid){
							try {
	
								String m= getUsedMemoryPid(pid);
	
								ressource+=" "+m ;
							} catch (FileNotFoundException e) {
								// Most probably the process doesn't exist anymore,
								// We reset the information of the process to default value
								Log.v(TAG,"The process is probably not running. Reset the information.");
								processinformation.reset();
								ressource+=" 0";
							}catch (Exception e) {
								e.printStackTrace();
								return "Unexpected error trying to read the memory";
							}
						}
						else{
							ressource+=" 0";
						}
						
						if (Integer.valueOf(Build.VERSION.SDK) >= 8) {
							if (isNetworkUsageByProcessOffsetSet() == false){
								initNetworkUsageByProcessOffset();
							}
							try {
								int uid = getPackageManager().getApplicationInfo(
										processinformation.getProcess_name(),0).uid;
								if (uid != -1) {
									long newTotalTx = getUidTxBytes(uid);
									long newTotalRx = getUidRxBytes(uid);
									long tx = newTotalTx - uidTxBytesOffsets.get(uid);
									if (tX < 0) tX = 0;
									long rx = newTotalRx - uidRxBytesOffsets.get(uid);
									if (rX < 0) rX = 0;
									uidTxBytesOffsets.put(uid, newTotalTx);
									uidRxBytesOffsets.put(uid, newTotalRx);
									ressource += " " + (tx);
									ressource += " " + (rx);
								} else {
									ressource += " -1";
									ressource += " -1";
								}
							} catch (NameNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								ressource += " -1";
								ressource += " -1";
							}
						} else {
							Log.v(TAG,"API level < 8 : No network data ");
						}
					} else { // Process is not running
						String nameprocess = processinformation.getProcess_name(); 
						ressource+="\n"+nameprocess+" -1 -1 -1 -1";
						
					}
				}
			}
			notifyGlobalChange(ressource,totalmem);
			//Log.v(TAG,ressource);
			return ressource+"\nEND";

		} else if (inputline.equals("DISPLAY")) {
			Log.v(TAG,"DISPLAY");

			/* First, get the Display from the WindowManager */  
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  

			
			/* Now we can retrieve all display-related informations */  
			int width = display.getWidth();  
			int height = display.getHeight();  
			int orientation = display.getOrientation();
			return "width "+width+" height "+height+" orientation "+orientation;
			
		} else if (inputline.matches("RANDOMLIST")) {
			Log.v(TAG,"RANDOMLIST");
			String packageList ="";
			PackageManager pm = this._context.getPackageManager();
			List<String> mainPack = new ArrayList<String>();
			Intent mainItent = new Intent(Intent.ACTION_MAIN);
			mainItent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> mainApps = pm.queryIntentActivities(mainItent,0);
			for (int i=0; i<mainApps.size(); i++) {
				ResolveInfo ri = mainApps.get(i);
				String packName = ri.activityInfo.applicationInfo.packageName;
				if (!mainPack.contains(packName)) {
					//Log.v(TAG,"Package:"+ri.activityInfo.applicationInfo.packageName);
					packageList += "\n"+ri.activityInfo.applicationInfo.packageName;
					mainPack.add(packName);
				}
					
			}	
			mainItent = new Intent(Intent.ACTION_MAIN);
			mainItent.addCategory(Intent.CATEGORY_MONKEY);
			mainApps = pm.queryIntentActivities(mainItent,0);
			for (int i=0; i<mainApps.size(); i++) {
				ResolveInfo ri = mainApps.get(i);
				String packName = ri.activityInfo.applicationInfo.packageName;
				if (!mainPack.contains(packName)) {
					//Log.v(TAG,"Package:"+ri.activityInfo.applicationInfo.packageName);
					packageList += "\n"+ri.activityInfo.applicationInfo.packageName;
					mainPack.add(packName);
				}
					
			}	
	
			return mainPack.size()+packageList;
		}else if (inputline.matches("PROCESSLIST")) {
			Log.v(TAG,"PROCESSLIST");
			PackageManager pm = this._context.getPackageManager();
			Vector<String> processApps = new Vector<String>();
			List<ApplicationInfo> pis = pm.getInstalledApplications(PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
			for (int i=0; i< pis.size(); i++) {
				ApplicationInfo pi = pis.get(i);
				if (!processApps.contains(pi.processName)) processApps.add(pi.processName);
			}
			String processList ="";
			for (int i=0; i<processApps.size(); i++) {
				Log.v(TAG,"Process:"+processApps.get(i));
				processList += "\n"+processApps.get(i);
			}
			return processApps.size()+processList;
		} else if (inputline.matches("PROCESSINFO")) {
			Log.v(TAG,"PROCESSINFO");
			PackageManager pm = this._context.getPackageManager();
			List<ApplicationInfo> pis = pm.getInstalledApplications(PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
			String processInfo ="";
			for (int i=0; i< pis.size(); i++) {
				ApplicationInfo pi = pis.get(i);
				processInfo += "\n"+pi.processName+" "+pi.packageName;
				Log.v(TAG,"Process:"+pi.processName+" "+pi.packageName);
			}
			return pis.size()+processInfo;
		}
		else if (inputline.matches("ENABLEFLIGHTMODE")) {
			Log.v(TAG,"ENABLEFLIGHTMODE");
			setFlightMode(true);
			return "OK";
		}
        else if (inputline.matches("DISABLEFLIGHTMODE")) {
            Log.v(TAG,"DISABLEFLIGHTMODE");
            setFlightMode(false);
            return "OK";
        }
        else if (inputline.matches("OFFLINE")) {
            Log.v(TAG,"OFFLINE");
            return "OK";
        }
		else {
			Log.v(TAG,"Unknown command");
			return "Unknown";
		}
	}
	
	private boolean isNetworkUsageByProcessOffsetSet(){
		if (uidRxBytesOffsets.isEmpty() && uidTxBytesOffsets.isEmpty()){
			return false;
		}else {
			return true;
		}
	}
	
	private void initNetworkUsageByProcessOffset(){
		// initialize uid network stats infos
		LinkedList<ProcessInformation> listprocess = cput
				.getListprocess();
		for (Iterator<ProcessInformation> iterator = listprocess
				.iterator(); iterator.hasNext();) {
			ProcessInformation processInformation = (ProcessInformation) iterator
					.next();
			if (processInformation.isRunning(this._context)) {
				try {
					String processName = processInformation.getProcess_name();
					int uid = getPackageManager().getApplicationInfo(
							processName, 0).uid;
					long rx=getUidRxBytes(uid);
					long tx=getUidTxBytes(uid);
					uidRxBytesOffsets.put(uid, rx);
					uidTxBytesOffsets.put(uid, tx);
//					Log.v(TAG, "Process="+processName+", Rx="+rx+", Tx="+tx);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

     private void setFlightMode(boolean on)
     {
        boolean isFlightModeOn = Settings.System.getInt(_context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		if (isFlightModeOn) Log.v(TAG,"Flight mode is on");
		else Log.v(TAG,"Flight mode is off");
        if(isFlightModeOn && !on) {
        	 Log.v(TAG,"Trying to set Flight mode off ...");
             Settings.System.putInt(_context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        	 Log.v(TAG,"system settings set");
        	 Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
             intent.putExtra("state", 0);
             _context.sendBroadcast(intent);
        	 Log.v(TAG,"intent sent to broadcast");
             return;
        } else if(!isFlightModeOn && on) { 
          	 Log.v(TAG,"Trying to set Flight mode on ...");
          	 Settings.System.putInt(_context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
           	 Log.v(TAG,"system settings set");
    	 	 Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    	 	 intent.putExtra("state", 1);
    	 	 _context.sendBroadcast(intent);
           	 Log.v(TAG,"intent sent to broadcast");
           	 return;
        }
     }
     
     /**
      * Only valid on API >= 8
      * @param uid process uid
      * @return transfered bytes for this uid
      */
     private long getUidTxBytes(int uid){
    	 String className = "android.net.TrafficStats";
  		String methodName = "getUidTxBytes";
  		try {
  			Class<?> handler = Class.forName(className);
  			Method m = handler.getDeclaredMethod(methodName, int.class);
  			long result = (Long) m.invoke(handler.newInstance(),uid);
  			return result;
  		} catch (Exception e) {
  			e.printStackTrace();
  			return -1;
  		}
     }
     
     /**
      * Only valid on API >= 8
      * @param uid process uid
      * @return received bytes for this uid
      */
     private long getUidRxBytes(int uid){
    	 String className = "android.net.TrafficStats";
  		String methodName = "getUidRxBytes";
  		try {
  			Class<?> handler = Class.forName(className);
  			Method m = handler.getDeclaredMethod(methodName, int.class);
  			long result = (Long) m.invoke(handler.newInstance(),uid);
  			return result;
  		} catch (Exception e) {
  			e.printStackTrace();
  			return -1;
  		}
     }

     /**
 	 * Only valid on API >= 8
 	 * @return
 	 */
 	private long getTotalRxBytes() {
 		String className = "android.net.TrafficStats";
 		String methodName = "getTotalRxBytes";
 		try {
 			Class<?> handler = Class.forName(className);
 			Method m = handler.getDeclaredMethod(methodName, (Class[]) null);
 			long result = (Long) m.invoke(handler.newInstance());
 			return result;
 		} catch (Exception e) {
 			e.printStackTrace();
 			return -1;
 		}
 	}

 	/**
 	 * Only valid on API >= 8
 	 * @return
 	 */
 	private long getTotalTxBytes() {
 		String className = "android.net.TrafficStats";
 		String methodName = "getTotalTxBytes";
 		try {
 			Class<?> handler = Class.forName(className);
 			Method m = handler.getDeclaredMethod(methodName, (Class[]) null);
 			long result = (Long) m.invoke(handler.newInstance());
 			return result;
 		} catch (Exception e) {
 			e.printStackTrace();
 			return -1;
 		}
 	}
     
	private String getUsedMemoryPid(int pid) throws IOException {
		int x = Integer.valueOf(Build.VERSION.SDK);
		if ( x >= 6) {
			try {
				Log.v(TAG,"getUsedMemoryPid("+pid+")");
				int [] pids = {pid};
				ActivityManager am = (ActivityManager) getSystemService("activity");
				// Method is invocated dynamically, because on 1.5 platforms it is not available
				Method[] methods = am.getClass().getMethods();
				for (Method method : methods) {
					if(method.getName().equals("getProcessMemoryInfo")) {
						Debug.MemoryInfo [] meminfo = (MemoryInfo[]) method.invoke(am, pids);
						Method m = meminfo[0].getClass().getMethod("getTotalPrivateDirty",(Class[])null);
						String mem = Integer.toString((Integer)m.invoke(meminfo[0], (Object[])null));
						//Some phones need the application to be sign
						//we use the old method for those applications
						if(mem.equals("0")){
							FileReader fileReader = new FileReader("/proc/"+pid+"/status");
							BufferedReader in = new BufferedReader( fileReader, 50);			
							String ret = null;

							//Skip the first 14 lines
							for(int j=0; j<15; j++)
								ret = in.readLine();
							fileReader.close();
							//VmRSS = Virtual Memory Resident Stack Size
							//Taille de la m�moire physique utilis�e.
							mem = ret.split(" +")[1];
							Log.v(TAG,"mem (1.5 VmRSS) = "+m);
						}
						else
							Log.v(TAG,"mem (2.1 TotalPrivateDirty) = "+mem);
						return mem;
					}
				}			 
				return "0";
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "ActivityManager problem";
			}
		} else if (x< 6){
			FileReader fileReader = new FileReader("/proc/"+pid+"/status");
			BufferedReader in = new BufferedReader( fileReader, 50);			
			String ret = null;

			//Skip the first 14 lines
			for(int j=0; j<15; j++)
				ret = in.readLine();
			fileReader.close();
			String  m = ret.split(" +")[1];
			//VmRSS = Virtual Memory Resident Stack Size
			//Taille de la m�moire physique utilis�e.
			Log.v(TAG,"mem (1.5 VmRSS) = "+m);
			return m;
		} else 
			return "-1";
	}




	private String getTotalMemoryUsed() {
		FileReader fileReader;
		//Get memory used
		try {
			fileReader = new FileReader("/proc/meminfo");
			BufferedReader in = new BufferedReader( fileReader, 50);

			String [] x = in.readLine().split(" +"); 
			String [] y = in.readLine().split(" +");
			fileReader.close();
			totalmem = x[1];

			int memused = Integer.parseInt(x[1])-Integer.parseInt(y[1]);
			fileReader.close();
			return String.valueOf(memused);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "Error reading memory";
		}		
	}

	public String getCpu() {
		return cput.getCPU()+"";
	}
	public String getConnection() {
		return null;
	}
	public String getMem() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void batteryLevel() {
 
		//BatteryManager battery = (BatteryManager) _context.getSystemService(Context.POWER_SERVICE);
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				if (rawlevel >= 0 && scale > 0) {
					batteryLevel = (rawlevel * 100) / scale;
				}
				Log.v(TAG,"Battery Level: " + batteryLevel + "%");
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		_context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}


	/**
	 * 
	 * @param NameProcess
	 * @return The PID of the process (return -1 if the process was not found)
	 */
	public int getPidFromName(String NameProcess){
		ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		int PIDfound = -1;
		for(int i = 0; i < procInfos.size(); i++)
		{
			if(NameProcess.equals(procInfos.get(i).processName)){
				PIDfound = procInfos.get(i).pid;
				break;
			}
		}
		Log.v(TAG,"getPidFromName for process "+NameProcess+". PID="+PIDfound);
		return PIDfound;
	}

	@Override
	public void onCreate() {
		Log.i(TAG,"onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG,"onDestroy");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG,"onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.i(TAG,"onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG,"onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	void initBeforeLoop() {
		// We clear the process list to be sure nothing is left	
		cput = new CPUThread(1000);
		t = new Thread(cput);
		t.setName("ATKMonitorCPUThread");
		t.start();
	}
	
	@Override
	void releaseAfterLoop(){
		if (cput != null)	cput.quit();
	}

	@Override
	int getPort() {
		return PORT;
	}
}
