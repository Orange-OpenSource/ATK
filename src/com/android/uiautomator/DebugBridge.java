/*
 * Copyright (C) 2012 The Android Open Source Project
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
 */

package com.android.uiautomator;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.orange.atk.platform.Platform;


import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class DebugBridge {
	private static AndroidDebugBridge sDebugBridge;
	private static boolean setInitilised =false;
	private static String getAdbLocation() {

		Logger.getLogger(DebugBridge.class).debug("  "+Platform.getInstance().getDefaultADBLocation());
		return Platform.getInstance().getDefaultADBLocation();

	}

	public static void init() {
		String adbLocation = getAdbLocation();
		if (adbLocation != null) {
			AndroidDebugBridge.init(false /* debugger support */);
			sDebugBridge = AndroidDebugBridge.createBridge(adbLocation, false);
		}
	}

	public static void terminate() {
		if (sDebugBridge != null) {
			sDebugBridge = null;
			AndroidDebugBridge.terminate();
		}
	}

	public static boolean isInitialized() {
		if(!setInitilised)
			return sDebugBridge != null;
		else 
			return setInitilised;
	}

	public static List<IDevice> getDevices() {
		return Arrays.asList(sDebugBridge.getDevices());
	}
	public static void setInitialised(AndroidDebugBridge adb) {
		sDebugBridge=adb;
		setInitilised=true;
	}
}