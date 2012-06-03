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
 * File Name   : AndroidMonkeyDriver.java
 *
 * Created     : 06/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.Position;

public class AndroidMonkeyDriver extends AndroidDriver {
	SyncService syncService;
	private final String localScript = Platform.TMP_DIR+Platform.FILE_SEPARATOR+"scriptfile";
	private final String remoteScript = "/sdcard/scriptfile";

	public AndroidMonkeyDriver(String phoneModel, String version, IDevice d) throws PhoneException {
		super(phoneModel, version, d);
		try {
			syncService = d.getSyncService();
		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_GET_SYNC_SERVICE_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
			syncService = null;
			throw new PhoneException(error);
		} catch (TimeoutException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_GET_SYNC_SERVICE_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
			syncService = null;
			throw new PhoneException(error);
		} catch (AdbCommandRejectedException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_GET_SYNC_SERVICE_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
			syncService = null;
			throw new PhoneException(error);
		}
	}

	public void touchScreenSlide(List<Position> path) throws PhoneException  {
		if (DONT_USE_MONKEY) super.touchScreenSlide(path);
		else touchScreenMotion(path, false);
	}

	public void touchScreenDragnDrop(List<Position> path) throws PhoneException {
		if (DONT_USE_MONKEY) super.touchScreenDragnDrop(path);
		else touchScreenMotion(path, true);
	}

	private void touchScreenMotion(List<Position> path, boolean longPress) throws PhoneException {
		FileWriter fw=null;
		try {
			Logger.getLogger(this.getClass()).debug("Generating monkey scriptfile : "+localScript);
			fw = new FileWriter(new File(localScript));
			fw.write("type= raw event\n");
			if (longPress) fw.write("count= "+(path.size()+1)+"\n");
			else fw.write("count= "+path.size()+"\n");
			fw.write("speed= 1.0\n");
			fw.write("start data >>\n");
			fw.write("DispatchPointer(0,0,0,"+path.get(0).getX()+".0,"+path.get(0).getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
			if (longPress) 
				fw.write("UserWait("+path.get(1).getTime()+")\n");
			for (int i=1; i<path.size()-1; i++) 
				fw.write("DispatchPointer("+path.get(i).getTime()+","+path.get(i).getTime()+",2,"+path.get(i).getX()+".0,"+path.get(i).getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
			fw.write("DispatchPointer("+path.get(path.size()-1).getTime()+","+path.get(path.size()-1).getTime()+",1,"+path.get(path.size()-1).getX()+".0,"+path.get(path.size()-1).getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");

		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",localScript);
			ErrorManager.getInstance().addError(getClass().getName(), error, e); 
			throw new PhoneException(error);
		} finally {
			try {
				if(fw!=null)
					fw.close();
			} catch (IOException e) {}
		}
		//SyncResult result;
		try{
		syncService.pushFile(localScript, remoteScript, new ISyncProgressMonitor() {

			public void advance(int arg0) {
			}

			public boolean isCanceled() {
				return false;
			}

			public void start(int arg0) {
			}

			public void startSubTask(String arg0) {
			}

			public void stop() {
			}});
		}catch(SyncException e){
			String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error); 
			throw new PhoneException(error);
		} catch (IOException e) {
			String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error); 
			throw new PhoneException(error);
		} catch (TimeoutException e) {
			String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
			ErrorManager.getInstance().addError(getClass().getName(), error); 
			throw new PhoneException(error);
		}
		executeShellCommand("monkey -f "+remoteScript+" 1", false);

	}

	public void touchScreenPress(Position click) throws PhoneException  {
		if (USE_MONKEY_FOR_PRESS) {
			FileWriter fw=null;
			try {
				Logger.getLogger(this.getClass()).debug("Generating monkey scriptfile : "+localScript);
				fw = new FileWriter(new File(localScript));
				fw.write("type= raw event\n");
				fw.write("count= 3\n");
				fw.write("speed= 1.0\n");
				fw.write("start data >>\n");
				fw.write("DispatchPointer(0,0,0,"+click.getX()+".0,"+click.getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
				fw.write("UserWait("+click.getTime()+")\n");
				fw.write("DispatchPointer(0,0,1,"+click.getX()+".0,"+click.getY()+".0,1.0,0.1,0,0.1,0.1,0,0)\n");
			} catch (IOException e) {
				String error = ResourceManager.getInstance().getString("WRITING_FILE_FAILURE",localScript);
				ErrorManager.getInstance().addError(getClass().getName(), error, e); 
				throw new PhoneException(error);
			} finally{
				try {
					if(fw!=null)
						fw.close();
				} catch (IOException e) { }
			}


			try{
				syncService.pushFile(localScript, remoteScript, new ISyncProgressMonitor() {
		

					public void advance(int arg0) {
					}

					public boolean isCanceled() {
						return false;
					}

					public void start(int arg0) {
					}

					public void startSubTask(String arg0) {
					}

					public void stop() {
					}});
			}catch(SyncException e){
				String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
				ErrorManager.getInstance().addError(getClass().getName(), error); 
				throw new PhoneException(error);
			} catch (IOException e) {
				String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
				ErrorManager.getInstance().addError(getClass().getName(), error); 
				throw new PhoneException(error);
			} catch (TimeoutException e) {
				String error = e.getMessage()+"\n"+ResourceManager.getInstance().getString("ANDROID_TOUCH_REPLAY_FAILED");
				ErrorManager.getInstance().addError(getClass().getName(), error); 
				throw new PhoneException(error);
			}
			executeShellCommand("monkey -f "+remoteScript+" 1", false);
		} else super.touchScreenPress(click);
	}
}
