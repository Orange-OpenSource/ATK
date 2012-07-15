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
 * File Name   : ATKWizardService.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import com.orange.atk.wizard.IATKWizardCom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ATKWizardService extends Service implements Runnable {
	static String TAG = "ATKWizardService";
	static int PORT = 1358;
	
	protected Thread t;
	protected ServerSocket serverSocket;
	protected boolean _stop = false;

	private String keyName = "NULL";
	
	private final IATKWizardCom.Stub mBinder = new IATKWizardCom.Stub () {
		@Override
		public void setKeyName(String kn) throws RemoteException {
			Log.v(TAG,"Call setKeyName "+kn+" on Service");
			keyName = kn;
		}

		public void stop() throws RemoteException {
			quit();
		}
	};
	
	public IBinder onBind(Intent intent) {
		Log.v(TAG,"onBind");
		return mBinder;
	}

	/*public void setKeyName(String keyName) {
		Log.v(TAG,"keyName="+keyName);
		this.keyName = keyName;
	}*/
	
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v(TAG,"onStart");
		_stop = false;
		if (t==null) {
			t = new Thread(this);
			t.start();
		}

	}
	
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;
		do{
			try {    
				String inputline;
				Log.v(TAG,"run"); 
				serverSocket = ServerSocketFactory.getDefault().createServerSocket(PORT); 
				Log.v(TAG,"after serverSocket"); 
				socket = serverSocket.accept();
				Log.v(TAG,"after accept");
				if (_stop ) 
					throw new Exception("to finally");
				in = new BufferedReader( new InputStreamReader(socket.getInputStream()),50);
				out = new PrintWriter(socket.getOutputStream(),true);

				while ((inputline = in.readLine()) != null) {
					//reportConnection(""+socket.getInetAddress()+" "+inputline);
					Log.v(TAG,inputline);
					String outputLine = analyseInput(inputline);
					out.println(outputLine);
					if (_stop) break;
				}
			}
			catch (Exception e)
				{Log.v(TAG,"accept cancelled");}
			finally {
				Log.v(TAG,"finally");
				if (in != null)
					try {
						in.close();
						if (out != null) 
							out.close();
						if (socket != null) 
							socket.close();
						if (serverSocket != null) 
							serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		} while(!_stop);
	}

	protected void quit() {
		Log.v(TAG,"quit");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		_stop = true;	
		this.stopSelf();				
	}

	protected String analyseInput(String inputline) {
		if (inputline.equals("READY")) {
			return "OK";
		} else if (inputline.equals("KEYNAME")) {
			Log.v(TAG,"KEYNAME="+keyName);
			String result = keyName;
			keyName = "NULL";
			return result;
		} if (inputline.equals("DISPLAY")) {
			Log.v(TAG,"DISPLAY");

			/* First, get the Display from the WindowManager */  
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  

			
			/* Now we can retrieve all display-related informations */  
			int width = display.getWidth();  
			int height = display.getHeight();  
			return width+"\n"+height;
			
		} else {
			Log.v(TAG,"Unknown command");
			return "Unknown";
		}
	}
}