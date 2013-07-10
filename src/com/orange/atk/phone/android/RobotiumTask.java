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
 * File Name   : RobotiumTask.java
 *
 * Created     : 05/06/2013
 */
package com.orange.atk.phone.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;


public class RobotiumTask {
	private final static  int PORT_ATK_SOLO_GET_ALL_APK = 7777;
	private final static int PORT_ATK_SOLO_TESTING=8888;
	private final static int PORT_ATK_SOLO_GET_FOREGROUND_APP=8899;
	private static Boolean Start_Solo=false;
	private static Boolean isTestParamSet=false;
	public static String PackageName="";
	public static String MainActivityName="";
	public static String PackageSourceDir="";
	public static int VersionCode=-1;
	protected AndroidPhone androidPhone=null;
	protected IDevice adevice=null;
	private Socket socketg =null;
	private ObjectOutputStream 	out =null;
	private	ObjectInputStream  in = null;

	public RobotiumTask(AndroidPhone androidPhone) {
		this.androidPhone= androidPhone;
		adevice=androidPhone.adevice;
	}



	public void setApkToTestWithRobotiumParam(String packName, String activityName, String packsourceDir,int versionCode)
			throws PhoneException {
		PackageName=packName;
		MainActivityName=activityName;
		PackageSourceDir=packsourceDir;
		VersionCode=versionCode;
		isTestParamSet=true;
		Logger.getLogger(this.getClass() ).debug("the function setTestAPKWithRobotiumParam is called");
	}
	/**
	 * 
	 * @param command
	 * @throws PhoneException
	 */
	public void sendCommandToExecuteToSolo(Object [] commands) throws PhoneException {
		if(!isTestParamSet){
			Logger.getLogger(this.getClass()).debug("can't send command to solo you must specify apk to test ! the first command " +
					"must be StartRobotiumTestOn to init robotium test");
			return;
		}
		if(!Start_Solo) {
			if(checkRobotium("com.orange.atk.serviceSendEventToSolo")!=0){
				pushSendEventService();
			} 
			PrepareApkForRobotiumTest.prepareAPKForRobotiumGetViews(adevice,PackageName,MainActivityName,PackageSourceDir, "ATKTestingAPKWithRobotium.apk",VersionCode);

			pushATKSoloTest();

			String Scommand="am instrument -w com.orange.atk.soloTest/android.test.InstrumentationTestRunner";
			float version = Float.valueOf(adevice.getProperty("ro.build.version.release").substring(0,3));
			if (version >= 3.1) {
				Scommand += " -f 32";
			}
			androidPhone.executeShellCommand(Scommand);
			
			try {
				adevice.createForward(PORT_ATK_SOLO_TESTING,PORT_ATK_SOLO_TESTING);
			}catch (TimeoutException e) {
				Logger.getLogger(this.getClass() ).error("Timeout while setting port forwarding");
				throw new PhoneException("Can not communicate with soloTest ");
			}catch (AdbCommandRejectedException e) {
				Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
				throw new PhoneException("Can not communicate with soloTest");
			}catch (IOException e) {
				Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
				throw new PhoneException("Can not communicate with soloTest");
			}
			Logger.getLogger(this.getClass()).debug("soloTest  is launched on the device ..."); 
			Start_Solo=true;

			try {
				socketg = new Socket("127.0.0.1", PORT_ATK_SOLO_TESTING);
				out = new ObjectOutputStream(socketg.getOutputStream());
				in = new  ObjectInputStream(socketg.getInputStream());
				Logger.getLogger(this.getClass()).debug("<start sendind commands >");
				if(!((String) commands[0]).toLowerCase().contains("ExitSolo".toLowerCase())) {
					out.writeObject(commands);
					out.flush();
					Logger.getLogger(this.getClass()).debug(" "+in.readObject());
				} else {
					out.writeObject(commands);
					out.flush();
					Logger.getLogger(this.getClass()).debug(" "+in.readObject());
					out.close();
					in.close();
					Logger.getLogger(this.getClass()).debug("  < finish sending commands >");
					Start_Solo=false;
					isTestParamSet=false;
				}
			} catch (SocketException e) {
				throw new PhoneException(e.getMessage());
			} catch (IOException e) {
				throw new PhoneException(e.getMessage());
			}   catch (ClassNotFoundException e) {
				throw new PhoneException(e.getMessage());
			}

		}else {
			try {
				if(!((String) commands[0]).toLowerCase().contains("ExitSolo".toLowerCase()))  {
					out.writeObject(commands);
					out.flush();
					Logger.getLogger(this.getClass()).debug(" "+in.readObject());
				} else {
					out.writeObject(commands);
					out.flush();
					Logger.getLogger(this.getClass()).debug(" "+in.readObject());
					out.close();
					in.close();
					Logger.getLogger(this.getClass()).debug("  < finish sending commands >");
					Start_Solo=false;
					isTestParamSet=false;
				} 
			} catch (IOException e) {
				throw new PhoneException(e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new PhoneException(e.getMessage());
			}
		}
	}

	public ArrayList<String> getAllInstalledAPK() throws PhoneException {

		if(checkRobotium("com.orange.atk.serviceSendEventToSolo")!=0) {
			pushSendEventService();
		}
		ArrayList<String> Apks=new ArrayList<String>();
		String Scommand="am startservice -n com.orange.atk.serviceSendEventToSolo/.ServiceGetAllAPK";
		float version = Float.valueOf(adevice.getProperty("ro.build.version.release").substring(0,3));
		if (version >= 3.1) Scommand += " -f 32";
		androidPhone.executeShellCommand(Scommand); 
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			Logger.getLogger(this.getClass()).debug(e2.getMessage());
		}

		try {
			adevice.createForward(PORT_ATK_SOLO_GET_ALL_APK,PORT_ATK_SOLO_GET_ALL_APK);
		}catch (TimeoutException e) {
			Logger.getLogger(this.getClass() ).error("Timeout while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo ");
		}catch (AdbCommandRejectedException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo");
		}catch (IOException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo");
		}
		Logger.getLogger(this.getClass()).debug("service Send Event To Solo  is launched on the device ..."); 


		try {
			socketg = new Socket("127.0.0.1", PORT_ATK_SOLO_GET_ALL_APK);
			out = new ObjectOutputStream(socketg.getOutputStream());
			in = new  ObjectInputStream(socketg.getInputStream());
			Logger.getLogger(this.getClass()).debug("<start getting all apk >");
			out.writeObject("apks");
			out.flush();

			Apks=(ArrayList<String>) in.readObject();
			Apks.add(0, "Foreground App");
			out.close();
			in.close();
			socketg.close();
			Logger.getLogger(this.getClass()).debug("\n < finish getting all apk>");
		} catch (UnknownHostException e) {
			throw new PhoneException(e.getMessage());
		} catch (IOException e) {
			throw new PhoneException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new PhoneException(e.getMessage());
		} 
		return Apks;
	}
	/**
	 * 
	 * @param packg
	 * @return
	 */
	private int checkRobotium(String packg) {
		String startCmd = "pm list packages";
		try {
			String[] result = androidPhone.executeShellCommand(startCmd,true);
			for(String res: result){
				if(res.contains(packg)){
					Logger.getLogger(this.getClass()).debug(packg+ " found");
					return 0;
				}
			}
		}catch (PhoneException e){
			Logger.getLogger(this.getClass()).debug("unable to check "+packg );
		}
		Logger.getLogger(this.getClass()).debug(packg+" not found");
		return -1;
	}


	private void pushATKSoloTest() throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Pushing ATK Solo Test on phone");
		try {
			String result = adevice.uninstallPackage("com.orange.atk.soloTest");
			if(result!=null){
				Logger.getLogger(this.getClass()).debug("Result of the uninstall: "+result);
			}
			result = adevice.installPackage(Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools"+Platform.FILE_SEPARATOR+
					"UiautomatorViewerTask" +Platform.FILE_SEPARATOR+"TempAPK" +Platform.FILE_SEPARATOR+"ATKTestingAPKWithRobotium.apk", true);
			if(result!=null){
				Logger.getLogger(this.getClass()).debug("Result of the push: "+result);
			}
		} catch (InstallException e){
			throw new PhoneException("ATK Solo Test - unable to install ATK Solo Test");
		}
	}

	/**
	 * 
	 * @throws PhoneException
	 */
	private void pushSendEventService() throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Pushing ATK Send Event Service on phone");
		try {
			String result = adevice.uninstallPackage(" com.orange.atk.serviceSendEventToSolo");
			if(result!=null){
				Logger.getLogger(this.getClass()).debug("Result of the uninstall: "+result);
			}
			result = adevice.installPackage(Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools"+Platform.FILE_SEPARATOR+
					"ATKServiceSendEventToSolo.apk", true);
			if(result!=null){
				Logger.getLogger(this.getClass()).debug("Result of the push: "+result);
			}
		} catch (InstallException e) {
			throw new PhoneException("ATK Send Event Service - unable to install SendEventService");
		}
	}


	public ArrayList<String> getForegroundApp() throws PhoneException {
		if(checkRobotium("com.orange.atk.serviceSendEventToSolo")!=0) {
			pushSendEventService();
		}
		ArrayList<String> Apk=null;
		String Scommand="am startservice -n com.orange.atk.serviceSendEventToSolo/.ServiceGetForegroundApp";
		float version = Float.valueOf(adevice.getProperty("ro.build.version.release").substring(0,3));
		if (version >= 3.1) Scommand += " -f 32";
		androidPhone.executeShellCommand(Scommand);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			Logger.getLogger(this.getClass()).debug(e2.getMessage());
		}
		Logger.getLogger(this.getClass()).debug("service Send Event To Solo is launched on the device ...");
		try {
			adevice.createForward(PORT_ATK_SOLO_GET_FOREGROUND_APP,PORT_ATK_SOLO_GET_FOREGROUND_APP);
		}catch (TimeoutException e) {
			Logger.getLogger(this.getClass() ).error("Timeout while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo ");
		}catch (AdbCommandRejectedException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo");
		}catch (IOException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" while setting port forwarding");
			throw new PhoneException("Can not communicate with service Send Event To Solo");
		}
		Logger.getLogger(this.getClass()).debug("service Send Event To Solo  is launched on the device ..."); 

		try {

			socketg = new Socket("127.0.0.1", PORT_ATK_SOLO_GET_FOREGROUND_APP);
			out = new ObjectOutputStream(socketg.getOutputStream());
			in = new  ObjectInputStream(socketg.getInputStream());
			Logger.getLogger(this.getClass()).debug("<start getting Foreground app >");

			out.writeObject("apk");
			out.flush();
			Apk=(ArrayList<String>) in.readObject();
			out.close();
			in.close();
			socketg.close();
			Logger.getLogger(this.getClass()).debug("\n < finish getting Foreground app>");
		} catch (UnknownHostException e) {
			throw new PhoneException(e.getMessage());
		} catch (IOException e) {
			throw new PhoneException(e.getMessage());
		}catch (ClassNotFoundException e) {
			throw new PhoneException(e.getMessage());
		} 
		return Apk;
	}



}
