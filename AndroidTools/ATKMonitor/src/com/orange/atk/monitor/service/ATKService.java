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
 * File Name   : ATKService.java
 *
 * Created     : 17/02/2010
 * Author(s)   : Laurent Gottely
 */
package com.orange.atk.monitor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

abstract class ATKService extends Service {
	static String TAG;
	static int PORT;
	
	protected Thread t;
	protected ServerSocket serverSocket;
	protected boolean _stop = false;

	BufferedReader in = null;
	PrintWriter out = null;
	Socket socket = null;
	
	protected Context _context;  

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.v(TAG,"onStart");
		_context = this.getApplicationContext();
		_stop = false;
	}
	private void releaseSocket(){
		Log.v(TAG,"releaseSocket");
		if (in != null){
			try {
				in.close();
				if (out != null) 
					out.close();
				if (socket != null) 
					socket.close();
				if (serverSocket != null) 
					serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}
		}
		releaseAfterLoop();
	}
	public void run() {
		
		do{

			
			try {    
				String inputline;
				serverSocket = ServerSocketFactory.getDefault().createServerSocket(this.getPort());  
				socket = serverSocket.accept();
				initBeforeLoop();

				if (_stop ){
					break;
				}
				Log.v(TAG,"after accept");
				in = new BufferedReader( new InputStreamReader(socket.getInputStream()),50);
				out = new PrintWriter(socket.getOutputStream(),true);

				while ((inputline = in.readLine()) != null) {
					//reportConnection(""+socket.getInetAddress()+" "+inputline);
					String outputLine = analyseInput(inputline);
					out.println(outputLine);
					if (_stop) break;
				}
			}
			catch (BindException e){
				Log.e(TAG, "", e);
			}catch (IOException e){
				Log.e(TAG, "", e);
			}
			finally {
				releaseSocket();
			}
		}while(!_stop);
			releaseSocket();
	}

	abstract int getPort();
	
	abstract void initBeforeLoop();
	
	abstract void releaseAfterLoop();

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

	protected abstract String analyseInput(String inputline);

}