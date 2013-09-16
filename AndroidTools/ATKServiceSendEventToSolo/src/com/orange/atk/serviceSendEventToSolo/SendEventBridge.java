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
 * File Name   : SendEventBridge.java
 *
 * Created     : 02/05/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.orange.atk.serviceSendEventToSolo;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * this class  creates a socket connection to send event(commands) to the solo's instrumentation
 * via the service send event
 */
public class SendEventBridge extends Thread {
	/**
	 * The default port used to start send Event Bridge.
	 */
	private static final int SEND_EVENT_BRIDGE_DEFAULT_PORT = 8888;
	private static String logTag = "SendEventBridge";
	private ServerSocket mServer;
	private final int mPort;
	private static SendEventBridge sServer;
	private static Handler parentHandler;


	public static SendEventBridge get(Handler parentHandle) {
		parentHandler = parentHandle;
		sServer = new SendEventBridge(SendEventBridge.SEND_EVENT_BRIDGE_DEFAULT_PORT);
		return sServer;

	}
	public SendEventBridge() {
		mPort = -1;
	}
	public SendEventBridge(int port) {
		mPort = port;
	}
	public void run() {
		try {
			String command="";
			mServer=ServerSocketFactory.getDefault().createServerSocket(mPort);

			Socket client = mServer.accept();
			ObjectInputStream in = null;
			ObjectOutputStream out=null;

			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());

			do {
				String [] commands=null;
				try {
					commands =    (String[]) in.readObject();
				} catch (EOFException e){
					Log.e(logTag, "EOFException error : "+ e.getMessage(), e);
				}
				if(commands!=null) {
					command= commands[0];
					for(int i=0; i<commands.length; i++) {
						Log.w(logTag, "args " + commands[i]);
					}
				} else {
					commands=null;
					commands= new String[2];
					commands[0]="ExitSolo";
					commands[1]="0";
					Message messageToParent = new Message();
					Bundle messageData = new Bundle();
					messageToParent.what = 0;
					messageData.putStringArray("command",commands);
					messageToParent.setData(messageData);
					parentHandler.sendMessage(messageToParent);	
					break;
				}
				Message messageToParent = new Message();
				Bundle messageData = new Bundle();
				messageToParent.what = 0;
				messageData.putStringArray("command",commands);
				messageToParent.setData(messageData);
				parentHandler.sendMessage(messageToParent);
				out.writeObject("the command " + command +" is send.\n");
				out.flush();
			} while(!command.equalsIgnoreCase("ExitSolo"));

			in.close();
			out.close();
			mServer.close();

		} catch (IOException e) {
			Log.e(logTag, "Connection error: ", e);
		}catch (ClassNotFoundException e) {
			Log.e(logTag, "ClassNotFoundException error : ", e);
		}
	}

}
