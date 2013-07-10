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
 * File Name   : RobotiumTaskForViewer.java
 *
 * Created     : 05/06/2013
 */

package com.android.uiautomator.robotiumTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.uiautomator.UiAutomatorHelper;
import com.android.uiautomator.UiAutomatorViewer;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;

public class RobotiumTaskForViewer {
	private final static int PORT_ATK_SOLO_GET_VIEWS = 9999;
	private final static int PORT_ATK_SOLO_GET_FOREGROUND_APP = 8899;
	public static String PackageName = "";
	public static String MainActivityName = "";
	public static String PackageSourceDir = "";
	public static String[] packinfo = new String[2];
	public static int VersionCode = -1;
	protected IDevice adevice = null;
	private Socket socketg = null;
	private PrintWriter out = null;
	private BufferedReader br = null;
	private ArrayList<String> list;
	public static int windowOrientation = 0;
	public static String XmlViews = "";

	private IShellOutputReceiver multiLineReceiver = new MultiLineReceiver() {
		@Override
		public void processNewLines(String[] lines) {
			for (String line : lines)
				list.add(line);
		}

		public boolean isCancelled() {
			return false;
		}

	};
	public RobotiumTaskForViewer(IDevice device) {
		adevice = device;
	}

	protected String  [] executeShellCommand(String cmd, Boolean output) throws PhoneException {
		IShellOutputReceiver receiver;
		list = new ArrayList<String>();
		receiver = multiLineReceiver;

		try {
			adevice.executeShellCommand(cmd, receiver);
		} catch (TimeoutException e) {
			Logger.getLogger(this.getClass() ).debug("/****error while executing  : "+cmd + " :"+ e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (AdbCommandRejectedException e) {
			Logger.getLogger(this.getClass() ).debug("/****error  while executing  : "+cmd  +" :"+  e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (ShellCommandUnresponsiveException e) {
			Logger.getLogger(this.getClass() ).debug("/****error while executing  : "+cmd + " :"+  e.getMessage());
			if(!cmd.contains("am instrument")) {
				throw new PhoneException(e.getMessage());
			}
		} catch (IOException e) {
			Logger.getLogger(this.getClass() ).debug("/****error  while executing  : "+cmd + " :"+ e.getMessage());
			throw new PhoneException(e.getMessage());
		}

		if (output) {
			return   list.toArray(new String[]{});
		}
		return null;
	}


	public String getViewFromRobotium(String cmd) throws PhoneException{ 
		if(UiAutomatorViewer.dumpXMLFirstTime) {
			Logger.getLogger(this.getClass() ).debug("/****Robotium Prepares XMl DUMP FILE ***/ ");
			if(checkRobotium("com.orange.atk.serviceSendEventToSolo")!=0){
				pushSendEventService();
			}

			getForegroundApp();
			PrepareApkForRobotiumTest.prepareAPKForRobotiumGetViews(adevice,PackageName,MainActivityName,PackageSourceDir, "ATKGetViewAPKWithRobotium.apk",VersionCode);
			pushATKGetViewsSolo();

			String Scommand="am instrument -w com.orange.atk.soloGetViews/android.test.InstrumentationTestRunner";
			float version=Float.valueOf(adevice.getProperty("ro.build.version.release").substring(0,3));
			if (version >= 3.1) {Scommand += " -f 32";}

			executeShellCommand(Scommand,false);


			try {
				adevice.createForward(PORT_ATK_SOLO_GET_VIEWS,PORT_ATK_SOLO_GET_VIEWS);
			} catch (TimeoutException e) {
				Logger.getLogger(this.getClass() ).debug("/****Time out error while getting XML File***/ " + e.getMessage());
				throw new PhoneException(e.getMessage());
			} catch (AdbCommandRejectedException e) {
				Logger.getLogger(this.getClass() ).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			} catch (IOException e) {
				Logger.getLogger(this.getClass() ).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			}

			try {
				socketg = new Socket("127.0.0.1", PORT_ATK_SOLO_GET_VIEWS);
				out = new PrintWriter(socketg.getOutputStream(), true);
				br = new BufferedReader(new InputStreamReader(socketg.getInputStream()));
				if(UiAutomatorHelper.supportsUiAutomator(adevice)){
					if(cmd.equalsIgnoreCase("views")){
						XmlViews=null;
						return XmlViews;
					}
				}
				out.println(cmd);
				out.flush();
				XmlViews = br.readLine();
				Logger.getLogger(this.getClass()).debug(
						"/**** views ***/  " + XmlViews);

			} catch (UnknownHostException e) {
				Logger.getLogger(this.getClass()).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			}
			UiAutomatorViewer.dumpXMLFirstTime = false;
			return XmlViews;
		} else {
			if(UiAutomatorHelper.supportsUiAutomator(adevice)){
				if(cmd.equalsIgnoreCase("views")){
					XmlViews= null;
					return XmlViews;
				}
			}
			try {
				if (cmd.equalsIgnoreCase("exit")) {
					out.println(cmd);
					out.flush();
					XmlViews = br.readLine();
					Logger.getLogger(this.getClass()).debug(
							"/****views***/ " + XmlViews);
					out.close();
					br.close();
					socketg.close();
					UiAutomatorViewer.dumpXMLFirstTime = true;
					XmlViews = "";
				} else {
					out.println(cmd);
					out.flush();
					XmlViews = br.readLine();
					Logger.getLogger(this.getClass()).debug("/****result ***/" + XmlViews);
				}

			} catch (IOException e) {
				Logger.getLogger(this.getClass()).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			}
			return XmlViews;
		}

	}

	private int checkRobotium(String packg) throws PhoneException {

		String startCmd = "pm list packages";
		String[] result = executeShellCommand(startCmd, true);
		for (String res : result) {
			if (res.contains(packg)) {
				Logger.getLogger(this.getClass()).debug(" " + packg + " found");
				return 0;
			}
		}
		Logger.getLogger(this.getClass()).debug(" " + packg + " not found");
		return -1;
	}

	private void pushATKGetViewsSolo() throws PhoneException {
		Logger.getLogger(this.getClass()).debug("/****Pushing ATKGetView APK***/ ");
		try {
			String result = adevice.uninstallPackage("com.orange.atk.soloGetViews");
			if (result != null) {
				Logger.getLogger(this.getClass()).debug("/****resul of uninstall : " + result);
			}
			result = adevice.installPackage(Platform.getInstance().getJATKPath()
					+ Platform.FILE_SEPARATOR + "AndroidTools" + Platform.FILE_SEPARATOR +
					"UiautomatorViewerTask" + Platform.FILE_SEPARATOR + "TempAPK"
					+ Platform.FILE_SEPARATOR + "ATKGetViewAPKWithRobotium.apk", true);
			if (result != null) {
				Logger.getLogger(this.getClass()).debug("/****resul of install : " + result);
			}
		} catch (InstallException e) {
			Logger.getLogger(this.getClass()).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
	}

	private void pushSendEventService() throws PhoneException {
		try {
			String result = adevice.uninstallPackage(" com.orange.atk.serviceSendEventToSolo");
			if (result != null) {
				Logger.getLogger(this.getClass()).debug("/****resul of uninstall : " + result);
			}
			result = adevice.installPackage(Platform.getInstance().getJATKPath()
					+ Platform.FILE_SEPARATOR + "AndroidTools" + Platform.FILE_SEPARATOR +
					"ATKServiceSendEventToSolo.apk", true);
			if (result != null) {
				Logger.getLogger(this.getClass()).debug("/****resul of install : " + result);
			}
		} catch (InstallException e) {
			Logger.getLogger(this.getClass()).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		}
	}

	public ArrayList<String> getForegroundApp() throws PhoneException {
		if (checkRobotium("com.orange.atk.serviceSendEventToSolo") != 0) {
			pushSendEventService();
		}
		ArrayList<String> Apk;
		//String Scommand = "am startservice -n com.orange.atk.serviceSendEventToSolo/.ServiceGetForegroundApp";
		String Scommand = "am broadcast -a com.orange.atk.serviceSendEventToSolo.FOREGROUNDAPP -n com.orange.atk.serviceSendEventToSolo/.BReceiverForGetForeGroundApp";
		float version = Float.valueOf(adevice.getProperty("ro.build.version.release").substring(0,
				3));
		if (version >= 3.1)
			Scommand += " -f 32";
		executeShellCommand(Scommand,false);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			Logger.getLogger(this.getClass()).debug("/****error : " + e2.getMessage());
			throw new PhoneException(e2.getMessage());

		}

		try {
			adevice.createForward(PORT_ATK_SOLO_GET_FOREGROUND_APP,PORT_ATK_SOLO_GET_FOREGROUND_APP);
		} catch (TimeoutException e) {
			Logger.getLogger(this.getClass() ).debug("/****error while forwarding : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		} catch (AdbCommandRejectedException e) {
			Logger.getLogger(this.getClass() ).debug("/****error while forwarding : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		} catch (IOException e) {
			Logger.getLogger(this.getClass() ).debug("/****error while forwarding : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		}

		try {
			Socket socketg = new Socket("127.0.0.1", PORT_ATK_SOLO_GET_FOREGROUND_APP);
			ObjectOutputStream out = new ObjectOutputStream(socketg.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socketg.getInputStream());
			out.writeObject("Apk");
			out.flush();
			Apk = (ArrayList<String>) in.readObject();
			out.close();
			in.close();
			socketg.close();
			if(Apk.size()==4){
				PackageName=Apk.get(0);
				MainActivityName=Apk.get(1);
				PackageSourceDir=Apk.get(2);
				VersionCode=Integer.parseInt(Apk.get(3));
				Logger.getLogger(this.getClass() ).debug(" " +PackageName);
				Logger.getLogger(this.getClass() ).debug(" " +MainActivityName);
				Logger.getLogger(this.getClass() ).debug(" " +PackageSourceDir);
			} else {
				Logger.getLogger(this.getClass() ).debug("/**** Can get apk to test :***/ ");
				JOptionPane.showMessageDialog(UiAutomatorHelper.mViewer, "Can't get all needed informations about apk from device","Warning",JOptionPane.WARNING_MESSAGE);
				throw new PhoneException("Can't get all needed informations about apk from device");
			}
			if(PackageSourceDir.toLowerCase().startsWith("/system/")) {
				Logger.getLogger(this.getClass() ).debug("/**** Can not perform robotium test on system app :***/ ");
				throw new PhoneException("Can perform robotium test on system app");
			}
		} catch (UnknownHostException e) {
			Logger.getLogger(this.getClass()).debug("error : " + e.getMessage(), e);
			throw new PhoneException(e.getMessage());

		} catch (IOException e) {
			Logger.getLogger(this.getClass()).debug("error : " + e.getMessage(), e);
			throw new PhoneException(e.getMessage());

		} catch (ClassNotFoundException e) {
			throw new PhoneException(e.getMessage());
		}
		return Apk;
	}

}
