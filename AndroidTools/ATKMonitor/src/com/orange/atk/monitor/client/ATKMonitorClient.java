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
 * File Name   : ATKMonitorClient.java
 *
 * Created     : 17/02/2010
 * Author(s)   : Laurent Gottely
 */
package com.orange.atk.monitor.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.orange.atk.monitor.service.ATKMonitorService;
import com.orange.atk.monitor.IATKMonitorCom;
import com.orange.atk.monitor.IATKMonitorEventListener;
import com.orange.atk.monitor.R;

public class ATKMonitorClient extends Activity {
	private static final String TAG = "ATKMonitorClient"; 
	private IATKMonitorCom ATKMonitorApi;

	/**
	 * graphics assets
	 */
	private Handler handler;
	private TextView version;
	private TextView memory;
	private TextView cpu;
	private TextView ressource;

	private String versionStr ="";
	private String memStr ="";
	private String cpuStr ="";
	//private static final int MENU_QUIT = 0;
	private static final int MENU_START = 1;
	private static final int MENU_STOP = 2;
	private static boolean isServiceStarted =false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"on Create");

		// Set the layout for this activity.  You can find it
		// in res/layout/atk_monitor.xml
		setContentView(R.layout.atk_monitor);
		handler = new Handler();
	
		version= (TextView) findViewById(R.id.version);
		memory = (TextView) findViewById(R.id.memory);
		cpu = (TextView) findViewById(R.id.cpu);
		ressource = (TextView) findViewById(R.id.global);


		//Get the version
		try {
			PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			this.versionStr = pi.versionName;
		} catch (NameNotFoundException e) {
			Log.v(TAG, "Error : No version name in manifest");
		}
		
		version.setText("Version: "+versionStr);
		Log.v(TAG, "Version: "+versionStr);
		
		memStr = getResources().getString(R.string.memory);
		cpuStr = getResources().getString(R.string.cpu);
		//if (!isServiceStarted ) {
			Intent i = new Intent(this,ATKMonitorService.class);	
			Log.v(TAG,"onResume");

			//ComponentName startService = this.startService(i);
			if (this.bindService(i,apiConnection,BIND_AUTO_CREATE)) {
				Log.v(TAG,"ATKMonitorService binded");	
			}
						
		//}

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	public String getVersionStr() {
		return versionStr;
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {	
		super.onSaveInstanceState(outState);
		Log.v(TAG,"OnSaveInstanceState isservicestarted = "+isServiceStarted);

		outState.putBoolean("serviceStarted",isServiceStarted);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isServiceStarted = savedInstanceState.getBoolean("serviceStarted");
		Log.v(TAG,"OnRestoreInstanceState isservicestarted = "+isServiceStarted);

	}
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_START, 0, "Start").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_STOP, 0, "Stop").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	//	menu.add(0, MENU_QUIT, 0, "Quit").setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == null) {
			Log.v(TAG,"onOptionItemSelected item is null !!");
			return false;
		}
		Log.v(TAG,"onOptionItemSelected("+item.getItemId()+")");
		switch (item.getItemId()) {
//		case MENU_QUIT:
//			//quit();
//			return true;
		case MENU_START:
			start();
			return true;
		case MENU_STOP:
			stop();
			return true;
		}
		return false;
	}

	private void stop() {
		Log.v(TAG,"stop");
		
		if (ATKMonitorApi != null) {
			Log.v(TAG,"stop1");
			try {

				ATKMonitorApi.stop();

				Log.v(TAG,"after ATLMonitorApi.stop()");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				this.unbindService(apiConnection);
				Log.v(TAG,"stop_finally after unbindService(apiconnection)");
				isServiceStarted =false;
			}
		}

	}

	private ServiceConnection apiConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.v(TAG,"onServiceConnected");
			isServiceStarted = true;
			ATKMonitorApi = IATKMonitorCom.Stub.asInterface(service);
			try {
				ATKMonitorApi.addEventListener(eventListener);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(TAG,"onServiceConnected ATKMonitorApi = "+ATKMonitorApi);

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.v(TAG,"onServiceDisconnected");
			isServiceStarted = false;
			try {
				ATKMonitorApi.removeAllEventListeners();
			} catch (RemoteException e) {				
				e.printStackTrace();}
			ATKMonitorApi = null;
		}
	};

	private IATKMonitorEventListener eventListener = new IATKMonitorEventListener.Stub() {

		public void globalChanged(final String global,final String totalmem) throws RemoteException {
			if (global != null) {
				handler.post(new Runnable() {
					public void run() {
						String toprint = "";
						String [] read= global.split("[ |'\n']+");
						cpu.setText(cpuStr+" "+read[1]+"% @ "+read[2]+" BogoMips");
						memory.setText(memStr+" "+read[3]+" kB (of "+totalmem+" kB)");
						
						//we ignore the memory free in /data
						
						//Test if we don't have any process
						if(5 == read.length)
							return;
						//Test if we have a multiple of 3 
						if(0!=((read.length-5)%3)){
							toprint = "Error in the number of parameter for the process!";
							return;
						}
						for(int i = 5;i<read.length;i+=3){
							int cpuinb = Integer.parseInt(read[i+1])*Integer.parseInt(read[2]);
							cpuinb = cpuinb/100;
							toprint+=read[i]+":\n  CPU: "+read[i+1]+"% ("+cpuinb+" Mips)\n  MEM: "+read[i+2]+" kB\n";
						}
						ressource.setText(toprint);
					}
				});
			}
		}
	};

	private void start() {
		Log.v(TAG,"start");
		Intent i = new Intent(this,ATKMonitorService.class);	
		Log.v(TAG,"start2");

		//ComponentName startService = this.startService(i);
		if (this.bindService(i,apiConnection,BIND_AUTO_CREATE)) {
			Log.v(TAG,"ATKMonitorService binded");			
		} else {
			Log.v(TAG,"Service ATKMonitor not found");
			return;
		}

		Log.v(TAG,"service started");
	}
}
