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
 * File Name   : RecordingThread.java
 *
 * Created     : 25/11/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone.android;


import java.io.IOException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;


/**
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
public class RecordingThread extends Thread {
	IDevice thephone;
	String inputToListen;
	EventFilter filter;
	
	public RecordingThread(IDevice adevice, String input, EventFilter filter) {
		thephone = adevice;
		inputToListen = input;
		this.filter = filter;
		setName("recorder-"+adevice.getProperty("ro.build.product")+"-"+input) ;
	}


	public void stoprecording()
	{
		try {
			filter.setCancelled(true);
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("ANDROID_STOP_RECORDING_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
		}
	}
	
	
	@Override
	public void run() {
		try {
			DdmPreferences.setTimeOut(3000000);
			thephone.executeShellCommand("getevent -t "+inputToListen, filter);
		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_RECORDING_EVENT_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
		} catch (TimeoutException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_RECORDING_EVENT_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
		} catch (AdbCommandRejectedException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_RECORDING_EVENT_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
		} catch (ShellCommandUnresponsiveException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_RECORDING_EVENT_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
		}
		
	}
	
}
