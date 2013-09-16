/**
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
 * File Name   : ServiceSendEvent.java
 *
 * Created     : 02/05/2013
 * Author(s)   : France Telecom
 */
package com.orange.atk.serviceSendEventToSolo;

import com.orange.atk.serviceSendEventToSolo.IServiceSendEvent;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

//simple service to get the command from socket connection and send it to  robotium test
public class ServiceSendEvent extends Service {
	private String[] command;
	private String commandViews;
	private boolean changeViews =false;
	private boolean change =false;

	private static String logTag="REMOTE SERVICE SEND EVENT TO SOLO";
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(logTag, "On bind INVOKED");
		return mBinder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		SendEventBridge.get(mainHandler).start();
		SendViewsBridge.get(mainHandlerViews).start();
		Log.d(logTag, "On create INVOKED");

	}

	//To receive command  from the thread SenCommandBrigde
	public Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				command=msg.getData().getStringArray("command");
				change=true;            
			}
		};
	};
	//To receive command from the thread SendViewsBridge
	public Handler mainHandlerViews = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				commandViews=msg.getData().getString("command");
				changeViews=true;            
			}
		};
	};
	private final IServiceSendEvent.Stub mBinder=new IServiceSendEvent.Stub() {
		@Override
		public String[] getEvent() throws RemoteException {
			if(change) {
				change=false;
				return command;
			} else {
				return null;
			}

		}

		@Override
		public void setViews(String Views) throws RemoteException {
			WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			Display mDisplay = mWindowManager.getDefaultDisplay();

			if( mDisplay.getOrientation()>0){
				SendViewsBridge.setCurrentViews(modifyWindowOrientation(Views, mDisplay.getOrientation()));
			} else {
				SendViewsBridge.setCurrentViews(Views);
			}
			Log.d(logTag, " setViews(String Views) INVOKED");
		}

		@Override
		public String getViewsCommand() throws RemoteException {
			if(changeViews) {
				changeViews=false;
				return commandViews;
			} else {
				return "";
			}
		}
	}; 

	public static String  modifyWindowOrientation(String ViewsXml, int orientation ) {
		String TextToSearch="<hierarchy rotation=\"0\">";
		String views=ViewsXml;
		if(views.contains(TextToSearch)) {
			String avant= views.substring(0,views.indexOf(TextToSearch));
			String apres = views.substring(views.indexOf(TextToSearch)+TextToSearch.length());
			views= avant+"<hierarchy rotation=\""+orientation+"\">"+apres;
		}	
		return views;
	}


}
