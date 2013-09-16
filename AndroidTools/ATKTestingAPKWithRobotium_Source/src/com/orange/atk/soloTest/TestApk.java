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
 * File Name   : TestApk.java
 *
 * Created     : 02/05/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.orange.atk.soloTest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jayway.android.robotium.solo.Solo;
import com.orange.atk.serviceSendEventToSolo.IServiceSendEvent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
@SuppressWarnings("unchecked")
public class TestApk extends ActivityInstrumentationTestCase2{
	private static  String TARGET_PACKAGE_ID;
	private static Class launcherActivityClass;
	private Solo solo;
	private static String logTag="Testing APK with ROBOTIUM";
	protected ServiceConnection remoteConnection=null;
	private ExecuteSoloCommand executeSoloCommand;
	private IServiceSendEvent serviceb=null;
	private String servicePackageName="com.orange.atk.serviceSendEventToSolo";
	private String serviceClassName="com.orange.atk.serviceSendEventToSolo.ServiceSendEvent";
	private String[] cmd=null;
	private Context ctx;

	public TestApk()throws ClassNotFoundException {
		super(null,null);
	}

	@Override
	protected void setUp() {
		InputStream stream = TestApk.class.getResourceAsStream("init.prop");
		if(stream!=null){
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			try {
				line=br.readLine();
				if(line!=null){
					try{
						line=line.replace("\n", "");
						launcherActivityClass=Class.forName(line);
					} catch (ClassNotFoundException e){
						Log.e(logTag,e.getMessage(),e);
					} 
				}
				line=br.readLine();
				if(line!=null){
					TARGET_PACKAGE_ID=line.replace("\n", "");
				}
				br.close();
			} catch (IOException e) {
				Log.e(logTag,e.getMessage(),e);
			}
		}
		ctx=getInstrumentation().getContext();
		Intent intent = new Intent();
		intent.setClassName(servicePackageName,serviceClassName);
		remoteConnection  = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				serviceb = IServiceSendEvent.Stub.asInterface(service);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}  
		};
		ctx.bindService(intent, remoteConnection, Context.BIND_AUTO_CREATE);  
		Activity a= launchActivity(TARGET_PACKAGE_ID, launcherActivityClass, null);
	    super.setActivity(a);
		solo = new Solo(getInstrumentation(),getActivity());
		executeSoloCommand=new ExecuteSoloCommand(solo);

	}


	/**
	 * Black Box Test  Method
	 */
	public void testDisplayBlackBox() throws Throwable {  
       String exitCommand="";
		//getting commands from remote service  and execute them
		do {
			if(remoteConnection!=null) {
				try  {
					 cmd=serviceb.getEvent();
					 if(cmd!=null){
						 exitCommand=cmd[0];
						 if(!cmd[0].equalsIgnoreCase("ExitSolo")) {
							 executeSoloCommand.execute(cmd);
							 Log.d(logTag, cmd[0]);
							}
					 }
					
				} catch (RemoteException e) {
					Log.d(logTag,e.getMessage(),e);	
				}
			}
		} while(!exitCommand.equalsIgnoreCase("ExitSolo"));


	}
	
	@Override
	public void tearDown() {
		solo.finishOpenedActivities();
		ctx.unbindService(remoteConnection);
	}

}