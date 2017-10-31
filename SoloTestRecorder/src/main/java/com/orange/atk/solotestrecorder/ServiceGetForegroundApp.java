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
 * File Name   : ServiceGetForegroundApp.java
 *
 * Created     : 02/05/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.orange.atk.solotestrecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class ServiceGetForegroundApp  extends Service {
	private static String logTag="REMOTE SERVICE GET CURRENT FOREGROUND APP";
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(logTag, "On bind INVOKED");
		return mBinder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		ArrayList<String> ForegroundApp = getCurrentForegroundApp();
		GetForegroundAppSocket.get(ForegroundApp).start();
		Log.d(logTag, "On create INVOKED");
		Log.d(logTag, ForegroundApp.toString());
	}

	@Override
	public void onStart(Intent intent, int startId)  {
		super.onStart(intent, startId);
		stopSelf();
		Log.d(logTag, "OnStart INVOKED");
	}

	private ArrayList<String> getCurrentForegroundApp() {
		ArrayList<String> AppInfo = new ArrayList<String>();
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
		for (PackageInfo packageInfo : installedPackages) {
			if(packageInfo.packageName.equalsIgnoreCase(componentInfo.getPackageName())) {
				AppInfo.add( componentInfo.getPackageName());
				AppInfo.add( taskInfo.get(0).topActivity.getClassName());
				AppInfo.add(packageInfo.applicationInfo.sourceDir);
				AppInfo.add(String.valueOf(packageInfo.versionCode));
				return AppInfo;
			}
		} 
		AppInfo.add( componentInfo.getPackageName());
		AppInfo.add( taskInfo.get(0).topActivity.getClassName());
		return AppInfo;

	}

	private final IServiceGetAllAPK.Stub mBinder = new IServiceGetAllAPK.Stub() {
		
	};
}