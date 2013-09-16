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
 * File Name   : GetAllAPKSocketBridge.java
 *
 * Created     : 02/05/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.orange.atk.serviceSendEventToSolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ServerSocketFactory;
import android.util.Log;

/**
 * 
 * this class  creates a socket connection to send all installed Apks to ATK script recorder
 */
public class GetAllAPKSocketBridge extends Thread {
	private static final int SEND_EVENT_BRIDGE_DEFAULT_PORT = 7777;

	private static final String logTag = "GetAllAPKSocketBridge";
	private ServerSocket mServer;
	private final int mPort;
	private static GetAllAPKSocketBridge sServer;
	private static ArrayList<String> allApkInfo;

	public static GetAllAPKSocketBridge get(ArrayList<String> Views) {
		allApkInfo=Views;
		sServer = new GetAllAPKSocketBridge(GetAllAPKSocketBridge.SEND_EVENT_BRIDGE_DEFAULT_PORT);
		return sServer;
	}
	public GetAllAPKSocketBridge() {
		mPort = -1;
	}

	public GetAllAPKSocketBridge(int port) {
		mPort = port;
	}

	public void run() {
		try {
			mServer=ServerSocketFactory.getDefault().createServerSocket(mPort);
			Socket client = mServer.accept();
			ObjectInputStream in = null;
			ObjectOutputStream out=null;
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
			in.readObject();
			out.writeObject(allApkInfo);
			out.flush();
			in.close();
			out.close();
			mServer.close();

		} catch (IOException e) {
			Log.e(logTag, "IOException : ", e);
		}catch (ClassNotFoundException e) {
			Log.e(logTag, "ClassNotFoundException error : ", e);
		}
	}
}
