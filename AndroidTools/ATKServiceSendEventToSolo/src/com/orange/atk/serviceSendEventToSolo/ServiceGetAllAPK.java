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
 * File Name   : ServiceGetAllAPK.java
 *
 * Created     : 02/05/2013
 * Author(s)   : France Telecom
 */
package com.orange.atk.serviceSendEventToSolo;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

public class ServiceGetAllAPK extends Service {
	private static final String LOG_TAG = "REMOTE SERVICE GET ALL APK INFO";
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "On bind INVOKED");
		return mBinder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		ArrayList<String> installedPackageAndActivities = getAllInstalledActivities();
		GetAllAPKSocketBridge.get(installedPackageAndActivities).start();
		Log.d(LOG_TAG, "On create INVOKED");
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		stopSelf();
		Log.d(LOG_TAG, "OnStart INVOKED");
	}

	private ArrayList<String> getAllInstalledActivities() {

		ArrayList<String> packageList =new ArrayList<String>();
		PackageManager pm = this.getApplicationContext().getPackageManager();
		
		ArrayList<String> mainPack = new ArrayList<String>();
		Intent mainItent = new Intent(Intent.ACTION_MAIN);
		mainItent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mainApps = pm.queryIntentActivities(mainItent,0);
		for (int i=0; i<mainApps.size(); i++) {
			ResolveInfo resolveInfo = mainApps.get(i);
			String packName = resolveInfo.activityInfo.applicationInfo.packageName;
			int VersionCode=0;
			try {
				PackageInfo pi =pm.getPackageInfo(packName, 0);
				Drawable d = pi.applicationInfo.loadIcon(pm);
				VersionCode=pi.versionCode;
			} catch (NameNotFoundException e) {
				Log.w(LOG_TAG, "NameNotFoundException "+e.getMessage());
			}
			if (!mainPack.contains(packName)) {
				String packgInfo = "";
				//Log.v(TAG,"Package:"+resolveInfo.activityInfo.applicationInfo.packageName);
				String activityName = resolveInfo.activityInfo.name;
				//get package name, first main activity name, the installation path and the version code
				packgInfo+=resolveInfo.activityInfo.applicationInfo.packageName+","+activityName+","
						+resolveInfo.activityInfo.applicationInfo.sourceDir+","+String.valueOf(VersionCode);
				packageList.add(packgInfo);
				mainPack.add(packName);
			}

		}	

		mainItent = new Intent(Intent.ACTION_MAIN);
		mainItent.addCategory(Intent.CATEGORY_MONKEY);
		mainApps = pm.queryIntentActivities(mainItent,0);
		for (int i=0; i<mainApps.size(); i++) {
			ResolveInfo ri = mainApps.get(i);
			String packName = ri.activityInfo.applicationInfo.packageName;
			int VersionCode=0;
			try {
				PackageInfo pi =pm.getPackageInfo(packName, 0);
				VersionCode=pi.versionCode;
			} catch (NameNotFoundException e) {
				Log.w(LOG_TAG, "NameNotFoundException "+e.getMessage());
			}
			if (!mainPack.contains(packName)) {
				String packgInfo = "";
				String activityName = ri.activityInfo.name;
				packgInfo+=ri.activityInfo.applicationInfo.packageName+","+activityName+","
						+ri.activityInfo.applicationInfo.sourceDir+","+String.valueOf(VersionCode);
				packageList.add(packgInfo);
				mainPack.add(packName);
			}
		}	

		return packageList;
	} 
	private final IServiceGetAllAPK.Stub mBinder = new IServiceGetAllAPK.Stub() {

	};
}
