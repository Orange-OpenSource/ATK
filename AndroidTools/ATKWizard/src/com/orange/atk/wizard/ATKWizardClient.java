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
 * File Name   : ATKWizardClient.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.wizard;

import java.util.Hashtable;

import com.orange.atk.wizard.IATKWizardCom;
import com.orange.atk.wizard.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ATKWizardClient extends Activity {
	private static final String TAG = "ATKWizardClient"; 

	private TextView version;
	private String versionStr ="";
	private String keyCodeStr ="";
	private TextView instructions;
	private TextView keycode;
	private Hashtable keyEventCodes = new KeyEventCodes().keyEventCodes;
	private IATKWizardCom ATKWizardApi;
	private static final int MENU_STOP = 1;

	private ServiceConnection apiConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.v(TAG,"onServiceConnected");
			ATKWizardApi = IATKWizardCom.Stub.asInterface(service);
			Log.v(TAG,"onServiceConnected ATKWizardApi = "+ATKWizardApi);
		}
		public void onServiceDisconnected(ComponentName className) {
			Log.v(TAG,"onServiceDisconnected");
			ATKWizardApi = null;
		}
	};

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.v(TAG,"on Create");
		
		setContentView(R.layout.main);
		version= (TextView) findViewById(R.id.version);
		instructions= (TextView) findViewById(R.id.instructions);
		keycode= (TextView) findViewById(R.id.keycode);
		
		//Get the version
		try {
			PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			this.versionStr = pi.versionName;
		} catch (NameNotFoundException e) {
			Log.v(TAG, "Error : No version name in manifest");
		}
		version.setText("Version: "+versionStr);
		Log.v(TAG, "Version: "+versionStr);

		instructions.setText("Press your phone keys");
		
		Intent i = new Intent(this,ATKWizardService.class);	
		Log.v(TAG,"startWizardService");
		startService(i);

		if (this.bindService(i,apiConnection,BIND_AUTO_CREATE)) {
			Log.v(TAG,"ATKWizardService binded");	
		}

		
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		Log.v(TAG,"onKeyDown =============================>"+keyCode);
		String keyName = (String) keyEventCodes.get(new Integer(keyCode));
		keyCodeStr = "Key pressed : "+keyName;
		keycode.setText(keyCodeStr);
		try {
			Log.v(TAG,"call service setKeyname="+keyName);
			ATKWizardApi.setKeyName(keyName);
		} catch (RemoteException e) {
			Log.v(TAG,"REMOTE EXCEPTION : "+e.getMessage());
			e.printStackTrace();
		}
		if (keyName.equals("MENU")) super.onKeyDown(keyCode, event);
		return true;
	}
	
	private void stop() {
		Log.v(TAG,"stop");

		
		if (ATKWizardApi != null) {
			Log.v(TAG,"stop wizard service");
			try {
				ATKWizardApi.stop();
				Log.v(TAG,"after ATKWizardApi.stop()");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				this.unbindService(apiConnection);
				Log.v(TAG,"stop_finally after unbindService(apiconnection)");
			}
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {

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
			case MENU_STOP:
				stop();
				return true;
			}
		return false;
	}


/*	protected void onResume() {
		Log.v(TAG,"on Resume");
		super.onResume();
		keycode= (TextView) findViewById(R.id.version);
		keycode.setText(keyCodeStr);
	}
	
	protected void onPause() {
		Log.v(TAG,"on Pause");
		keycode.setText("Key pressed : HOME");
		super.onPause();
	}
	*/


}