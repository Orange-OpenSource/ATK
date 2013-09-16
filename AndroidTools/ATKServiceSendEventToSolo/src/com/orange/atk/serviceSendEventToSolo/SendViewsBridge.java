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
 * File Name   : SendViewsBridge.java
 *
 * Created     : 02/05/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.orange.atk.serviceSendEventToSolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class SendViewsBridge extends Thread {

	private static final int SEND_EVENT_BRIDGE_DEFAULT_PORT = 9999;
	private static final String logTag = "SendViewsBridge";
	private ServerSocket mServer;
	private final int mPort;
	private static SendViewsBridge sServer;
	private static String currentViews="";
	private static Handler parentHandler;



	public static SendViewsBridge get(Handler parentHandle) {
		parentHandler = parentHandle;
		sServer = new SendViewsBridge(SendViewsBridge.SEND_EVENT_BRIDGE_DEFAULT_PORT);
		return sServer;
	}

	public static void setCurrentViews(String currentViews) {
		SendViewsBridge.currentViews = currentViews;
	}
	public SendViewsBridge() {
		mPort = -1;
	}

	public SendViewsBridge(int port) {
		mPort = port;
	}


	public void run() {
		try {
			String command="";
			mServer=ServerSocketFactory.getDefault().createServerSocket(mPort);
			Socket client = mServer.accept();
			BufferedReader in = null;
			PrintWriter out=null;
			in = new BufferedReader(new InputStreamReader(client.getInputStream()),1024);
			out = new PrintWriter(client.getOutputStream(), true);
			do {
				final String request = in.readLine();
				command=request;
				if ( command ==null){
					Message messageToParent = new Message();
					Bundle messageData = new Bundle();
					messageToParent.what = 0;
					messageData.putString("command","exit");
					messageToParent.setData(messageData);
					parentHandler.sendMessage(messageToParent);
					break;
				}
				Message messageToParent = new Message();
				Bundle messageData = new Bundle();
				messageToParent.what = 0;
				messageData.putString("command",request);
				messageToParent.setData(messageData);
				parentHandler.sendMessage(messageToParent);
				if(!command.equalsIgnoreCase("exit")){
				while(currentViews.length()<=0){
				}
				out.write(currentViews+"\n");
				currentViews="";
				} else {
					out.write(command +"\n");
				}
				out.flush();
			} while(!command.equalsIgnoreCase("exit"));
			in.close();
			out.close();
			mServer.close();

		} catch (IOException e) {
			Log.e(logTag, "Connection error: ", e); 

		}
	}


}
