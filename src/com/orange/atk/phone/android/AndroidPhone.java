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
 * File Name   : AndroidPhone.java
 *
 * Created     : 05/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.phone.android;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.atkUI.anaHopper.HopperStep;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.manageListener.IPhoneKeyListener;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.TcpdumpLineListener;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;

/**
 * Android generic driver. Need to be root on the phone to work.
 * 
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 * 
 */
public class AndroidPhone implements PhoneInterface {
	
	private RobotiumTask robotiumTask=null;

	private static final String ARO_APK_PATH = "ARO\\ARODataCollector_OpenSource_v2.2.1.1.apk";
	private final static EventListenerList listeners = new EventListenerList();
	private final static String ATK_MONITOR_VERSION = "2.9";
	private boolean isInitResDone = false;
	// private Boolean ispm = false;

	protected IDevice adevice = null;
	protected int cnxStatus = PhoneInterface.CNX_STATUS_DISCONNECTED;
	protected boolean isFailed = false;
	protected boolean isStarted = false;
	protected boolean isScriptRecording = false;
	// private ArrayList<Socket> listSocket;
	private Socket socket = null;
	protected final static int PORT_ATK_MONITOR = 1357;

	private String name;
	private String uid;
	// Used for ATK Monitor
	private PrintWriter outMonitor;
	private BufferedReader inMonitor;

	protected byte[] CRLF = {10, 13};
	protected byte[] CR = {13};
	private boolean disabledPhone = true;

	private IShellOutputReceiver shellOutputReceiver = new IShellOutputReceiver() {
		public void addOutput(byte[] data, int offset, int length) {
		}
		public void flush() {
		}
		public boolean isCancelled() {
			return false;
		}
	};
	private ArrayList<String> list;

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

	// duplet for Key Value
	class KeyValue {
		public String iconpath;
		public int code;
		public KeyValue(String icon, int number) {
			iconpath = icon;
			code = number;
		};
	};
	protected static final HashMap<String, KeyValue> keysAssociations = new HashMap<String, KeyValue>();

	// only use by child class
	protected AndroidPhone() {
	};

	public AndroidPhone(IDevice device) {
		// initialise HashMap
		keysAssociations.put("SOFT_LEFT", new KeyValue(null, 1)); // 1 Soft key
																	// 1
		keysAssociations.put("SOFT_RIGHT", new KeyValue(null, 2)); // 2 Soft key
																	// 2
		keysAssociations.put("HOME", new KeyValue("keyboard/HOME.png", 3));// 3
																			// Home
																			// key
		keysAssociations.put("BACK", new KeyValue("keyboard/CANCEL.png", 4));// 4
																				// Back
																				// key
		keysAssociations.put("CALL", new KeyValue("keyboard/hangup.png", 5));// 5
																				// Call
																				// key
		keysAssociations.put("END_CALL", new KeyValue("keyboard/hangdown.png", 6));// 6
																					// End
																					// call
																					// key
		keysAssociations.put("0", new KeyValue("keyboard/0.png", 7)); // 7
																		// Number
																		// keys
		keysAssociations.put("1", new KeyValue("keyboard/1.png", 8)); // 8
																		// Number
																		// keys
		keysAssociations.put("2", new KeyValue("keyboard/2.png", 9)); // 9
																		// Number
																		// keys
		keysAssociations.put("3", new KeyValue("keyboard/3.png", 10)); // 10
																		// Number
																		// keys
		keysAssociations.put("4", new KeyValue("keyboard/4.png", 11)); // 11
																		// Number
																		// keys
		keysAssociations.put("5", new KeyValue("keyboard/5.png", 12)); // 12
																		// Number
																		// keys
		keysAssociations.put("6", new KeyValue("keyboard/6.png", 13));// 13
																		// Number
																		// keys
		keysAssociations.put("7", new KeyValue("keyboard/7.png", 14));// 14
																		// Number
																		// keys
		keysAssociations.put("8", new KeyValue("keyboard/8.png", 15)); // 15
																		// Number
																		// keys
		keysAssociations.put("9", new KeyValue("keyboard/9.png", 16)); // 16
																		// Number
																		// keys
		keysAssociations.put("STAR", new KeyValue(null, 17)); // 17 Star
		keysAssociations.put("POUND", new KeyValue(null, 18)); // 18 Pound
		keysAssociations.put("DPAD_UP", new KeyValue(null, 19));// 19 Up arrow
		keysAssociations.put("DPAD_DOWN", new KeyValue(null, 20)); // 20 Down
																	// arrow
		keysAssociations.put("DPAD_LEFT", new KeyValue(null, 21));// 21 Left
																	// arrow
		keysAssociations.put("DPAD_RIGHT", new KeyValue(null, 22));// 22 Right
																	// arrow
		keysAssociations.put("DPAD_CENTER", new KeyValue(null, 23));// 23 Center
		keysAssociations.put("VOLUME_UP", new KeyValue(null, 24));// 24 Volume
																	// up
		keysAssociations.put("VOLUME_DOWN", new KeyValue(null, 25));// 25 Volume
																	// down
		keysAssociations.put("POWER", new KeyValue(null, 26)); // 26 Power
		keysAssociations.put("CAMERA", new KeyValue(null, 27));// 27 Camera
		keysAssociations.put("CLEAR", new KeyValue("keyboard/DEL.png", 28));// 28
																			// CLEAR
		keysAssociations.put("A", new KeyValue("keyboard/A.png", 29)); // 29 A
																		// key
		keysAssociations.put("B", new KeyValue("keyboard/B.png", 30));// 30 B
																		// key
		keysAssociations.put("C", new KeyValue("keyboard/C.png", 31));// 31 C
																		// key
		keysAssociations.put("D", new KeyValue("keyboard/D.png", 32));// 32 D
																		// key
		keysAssociations.put("E", new KeyValue("keyboard/E.png", 33));// 33 E
																		// key
		keysAssociations.put("F", new KeyValue("keyboard/F.png", 34));// 34 F
																		// key
		keysAssociations.put("G", new KeyValue("keyboard/G.png", 35));// 35 G
																		// key
		keysAssociations.put("H", new KeyValue("keyboard/H.png", 36));// 36 H
																		// key
		keysAssociations.put("I", new KeyValue("keyboard/I.png", 37));// 37 I
																		// key
		keysAssociations.put("J", new KeyValue("keyboard/J.png", 38)); // 38 J
																		// key
		keysAssociations.put("K", new KeyValue("keyboard/K.png", 39)); // 39 K
																		// key
		keysAssociations.put("L", new KeyValue("keyboard/L.png", 40));// 40 L
																		// key
		keysAssociations.put("M", new KeyValue("keyboard/M.png", 41));// 41 M
																		// key
		keysAssociations.put("N", new KeyValue("keyboard/N.png", 42)); // 42 N
																		// key
		keysAssociations.put("O", new KeyValue("keyboard/O.png", 43));// 43 O
																		// key
		keysAssociations.put("P", new KeyValue("keyboard/P.png", 44)); // 44 P
																		// key
		keysAssociations.put("Q", new KeyValue("keyboard/Q.png", 45));// 45 Q
																		// key
		keysAssociations.put("R", new KeyValue("keyboard/R.png", 46));// 46 R
																		// key
		keysAssociations.put("S", new KeyValue("keyboard/S.png", 47));// 47 S
																		// key
		keysAssociations.put("T", new KeyValue("keyboard/T.png", 48)); // 48 T
																		// key
		keysAssociations.put("U", new KeyValue("keyboard/U.png", 49)); // 49 U
																		// key
		keysAssociations.put("V", new KeyValue("keyboard/V.png", 50));// 50 V
																		// key
		keysAssociations.put("W", new KeyValue("keyboard/W.png", 51)); // 51 W
																		// key
		keysAssociations.put("X", new KeyValue("keyboard/X.png", 52));// 52 X
																		// key
		keysAssociations.put("Y", new KeyValue("keyboard/Y.png", 53));// 53 Y
																		// key
		keysAssociations.put("Z", new KeyValue("keyboard/Z.png", 54));// 54 Z
																		// key
		keysAssociations.put("COMMA", new KeyValue("keyboard/COMMA.png", 55));// 55
																				// Comma
																				// key
		keysAssociations.put("POINT", new KeyValue("keyboard/..png", 56)); // 56
																			// Period
																			// key
		keysAssociations.put("ALT_L", new KeyValue("keyboard/ALT.png", 57)); // 57
																				// ALT+Left
		keysAssociations.put("ALT_R", new KeyValue("keyboard/ALT.png", 58));// 58
																			// ALT+Right
		keysAssociations.put("SHIFT_L", new KeyValue("keyboard/SHIFT.png", 59));// 59
																				// SHIFT+Left
		keysAssociations.put("SHIFT_R", new KeyValue("keyboard/SHIFT.png", 60));// 60
																				// SHIFT+Right
		keysAssociations.put("TAB", new KeyValue(null, 61)); // 61 Tab key
		keysAssociations.put("SPACE", new KeyValue("keyboard/SPACE.png", 62)); // 62
																				// Space
																				// key
		keysAssociations.put("SYM", new KeyValue("keyboard/SYM.png", 63)); // 63
																			// SYM
																			// key
		keysAssociations.put("EXPLORER", new KeyValue(null, 64));// 64 EXPLORE
																	// key
		keysAssociations.put("ENVELOPE", new KeyValue(null, 65));// 65 ENVELOPE
																	// key
		keysAssociations.put("ENTER", new KeyValue("keyboard/RETURN.png", 66)); // 66
																				// ENTER
																				// key
		keysAssociations.put("DEL", new KeyValue("keyboard/DEL.png", 67)); // 67
																			// DEL
																			// key
		keysAssociations.put("GRAVE", new KeyValue(null, 68)); // 68 GRAVE key
		keysAssociations.put("MINUS", new KeyValue(null, 69));// 69 Minus key
		keysAssociations.put("EQUALS", new KeyValue(null, 70)); // 70 Equals key
		keysAssociations.put("LEFT_BRACKET", new KeyValue(null, 71));// 71 Left
																		// bracket
																		// key
		keysAssociations.put("RIGHT_BRACKET", new KeyValue(null, 72)); // 72
																		// Right
																		// bracket
																		// key
		keysAssociations.put("BACKSLASH", new KeyValue(null, 73)); // 73 \ Back
																	// slash key
		keysAssociations.put("SEMICOLON", new KeyValue(null, 74)); // 74
																	// Semicolon
																	// key
		keysAssociations.put("APOSTROPHE", new KeyValue(null, 75)); // 75
																	// Apostrophe
																	// key
		keysAssociations.put("SLASH", new KeyValue(null, 76)); // 76 Slash key
		keysAssociations.put("@", new KeyValue("keyboard/@.png", 77)); // 77 At
																		// key
		keysAssociations.put("NUM", new KeyValue(null, 78)); // 78 Num key
		keysAssociations.put("HEADSETHOOK", new KeyValue(null, 79)); // 79 Head
																		// set
																		// hook
																		// key
		keysAssociations.put("FOCUS", new KeyValue(null, 80)); // 80 Focus key
		keysAssociations.put("PLUS", new KeyValue(null, 81)); // 81 Plus key
		keysAssociations.put("MENU", new KeyValue("keyboard/MENU.png", 82)); // 82
																				// Menu
																				// key
		keysAssociations.put("NOTIFICATION", new KeyValue(null, 83)); // 83
																		// Notification
																		// key
		keysAssociations.put("SEARCH", new KeyValue("keyboard/search.png", 84)); // 84
																					// Search
																					// key

		// bring the Device found by the fabric
		adevice = device;
		robotiumTask= new RobotiumTask(this);
	}

	public boolean isDisabledPhone() {
		return disabledPhone;
	}

	public void setDisabledPhone(boolean disablePhone) {
		this.disabledPhone = disablePhone;
	}

	public void beep() throws PhoneException {
		// TODO: beep the phone
		Toolkit.getDefaultToolkit().beep();
	}

	public void keyUp(String key) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void keyDown(String key) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void keyPress(String key, int keyPressTime, int delay) throws PhoneException {
		long time = System.currentTimeMillis();
		KeyValue kv = keysAssociations.get(key);
		String cmd = null;

		if (kv != null) {
			int i = kv.code;
			cmd = "input keyevent " + i;
			if (6 == i)
				cmd = "radiooptions 10";

			executeShellCommand(cmd, false);
		} else {
			Logger.getLogger(this.getClass()).warn("Key : " + key + " not found in database");
		}
	}

	public void killMidlet(String midlet) throws PhoneException {
		// permission denied
		executeShellCommand("killall " + midlet, false);
	}

	public void setFlightMode(boolean on) throws PhoneException {

		try {
			openSocket(PORT_ATK_MONITOR);
			if (on)
				outMonitor.println("ENABLEFLIGHTMODE");
			else
				outMonitor.println("DISABLEFLIGHTMODE");
			String line = inMonitor.readLine();
			if (!line.contains("OK")) {
				String error = ResourceManager.getInstance().getString("XX_SCRIPT_COMMAND_FAILED",
						"setFlightMode(" + on + ")");
				ErrorManager.getInstance().addError(getClass().getName(), error);
			}
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("XX_SCRIPT_COMMAND_FAILED",
					"setFlightMode(" + on + ")");
			ErrorManager.getInstance().addError(getClass().getName(), error, e);
		}

	}

	public void reset() throws PhoneException {
		// TODO:
		// CommandExecutor cmd_exe = new CommandExecutor();
		// String cmd = "adb shell reboot";
		// cmd_exe.execute(cmd);
	}

	public void runMidlet(String midlet) throws PhoneException {
		if (midlet.startsWith("-a")) {
			executeShellCommand("am start " + midlet, false);
		} else {
			executeShellCommand("am start -n " + midlet, false);
		}
	}

	public BufferedImage screenShot() throws PhoneException {

		BufferedImage image = null;
		try {
			// [try to] ensure ADB is running
			RawImage rawImage = adevice.getScreenshot();

			// convert raw data to an Image
			image = new BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB);

			int index = 0;
			int increment = rawImage.bpp >> 3;

			for (int y = 0; y < rawImage.height; y++) {
				for (int x = 0; x < rawImage.width; x++) {
					image.setRGB(x, y, rawImage.getARGB(index));
					index += increment;
				}
			}

		} catch (Exception e) {
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCREENSHOT_ERROR"), e);
			throw new PhoneException(ResourceManager.getInstance().getString("SCREENSHOT_ERROR"));
		}
		return image;
	}

	/**
	 * 
	 * @param cmd
	 *            The shell command which will be executed by Android.
	 * @param output
	 *            . set it to True if you want the output of the command
	 * 
	 * @return The output of the shell command (line by line)
	 */
	protected String[] executeShellCommand(String cmd, Boolean output) throws PhoneException {

		IShellOutputReceiver receiver;
		list = new ArrayList<String>();

		// Logger.getLogger(this.getClass()
		// ).debug("executeShellCommand: "+cmd);

		if (output) {
			receiver = multiLineReceiver;
		} else {
			receiver = shellOutputReceiver;
		}

		try {
			adevice.executeShellCommand(cmd, receiver);

		} catch (IOException e) {
			String result = "";
			for (String temp : list)
				result += temp + "\r\n";
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (TimeoutException e) {
			String result = "";
			for (String temp : list)
				result += temp + "\r\n";
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (AdbCommandRejectedException e) {
			String result = "";
			for (String temp : list)
				result += temp + "\r\n";
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (ShellCommandUnresponsiveException e) {
			String result = "";
			for (String temp : list)
				result += temp + "\r\n";
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		}

		if (output) {
			return list.toArray(new String[]{});
		}
		return null;
	}

	/**
	 * 
	 * @param cmd
	 *            the shell command to execute on android device
	 * @param Pattern
	 *            . The pattern which is apply again output of shell command
	 * 
	 * @return the group(1) of the pattern.
	 * @throws PhoneException
	 */
	protected String findShellObject(String cmd, final String pattern) throws PhoneException {

		final String[] result = new String[1];
		IShellOutputReceiver receiver;

		receiver = new MultiLineReceiver() {
			Pattern pat = Pattern.compile(pattern);
			@Override
			public void processNewLines(String[] lines) {
				for (String line : lines) {
					Matcher mtc = pat.matcher(line);
					if (mtc.matches())
						result[0] = mtc.group(1);
				}

			}

			public boolean isCancelled() {
				return false;
			}

		};

		try {
			adevice.executeShellCommand(cmd, receiver);
		} catch (IOException e) {
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result[0]));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (TimeoutException e) {
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result[0]));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (AdbCommandRejectedException e) {
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result[0]));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		} catch (ShellCommandUnresponsiveException e) {
			ErrorManager.getInstance().addError(
					getClass().getName(),
					ResourceManager.getInstance().getString("COMMAND_XX_FAILED_RESULT_XX", cmd,
							result[0]));
			ErrorManager.getInstance().addError(getClass().getName(),
					ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			throw new PhoneException(ResourceManager.getInstance().getString(
					"SCRIPT_COMMAND_FAILURE"));
		}
		return result[0];
	}

	public boolean isMidletRunning(String MidletName) {

		return true;
	}

	public boolean startRandomTest(String hopperTest, String outputDir, ResultLogger mainLogger,
			Map<String, String> randomTestParam) throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Start random test");
		boolean result = false;
		isStarted = true;
		String lastLine = "";
		int nbofEvent = 2000;
		try {
			nbofEvent = Integer.parseInt(randomTestParam.get(HopperStep.PARAM_NBEVENTS));
		} catch (NumberFormatException e1) {
		}
		int throttle = 0;
		try {
			throttle = Integer.parseInt(randomTestParam.get(HopperStep.PARAM_THROTTLE));
		} catch (NumberFormatException e1) {
		}

		Runtime r = Runtime.getRuntime();
		if (hopperTest.contains(",")) {
			hopperTest = hopperTest.replaceAll(",", " -p ");
		}
		String[] args = {Platform.getInstance().getDefaultADBLocation(), "-s", this.uid, "shell",
				"monkey", "-v -s 3", "-p " + hopperTest, " --throttle " + throttle, "" + nbofEvent};
		/*
		 * Logger.getLogger(this.getClass()).debug("Start random test 2");
		 * ArrayList<String> args = new ArrayList<String>();
		 * args.add(AutomaticPhoneDetection.getInstance().getADBLocation());
		 * args.add("shell"); args.add("monkey"); args.add("-v -s 3");
		 * args.add("-p "+hopperTest); if (throttle!=0) {
		 * args.add(" --throttle "+throttle);
		 * 
		 * } args.add(""+nbofEvent);
		 */

		Process p;
		BufferedReader in = null;
		try {
			/*
			 * String[] params = (String[]) args.toArray(); String paramString =
			 * ""; for (int i=0; i<params.length; i++) paramString += params[i];
			 * Logger
			 * .getLogger(this.getClass()).debug("Command = "+paramString);
			 */
			p = r.exec(args);
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				if (line.contains("Sending event #")) {
					mainLogger.addInfotoActionLogger("Monkey", line, new Date(), new Date());
				} else if (line.contains("Events injected") || line.contains(":Dropped:")
						|| line.contains("Monkey")
						|| line.contains("System appears to have crashed")) {
					mainLogger.addInfotoActionLogger("Monkey", line, new Date(), new Date());
					mainLogger.addInfoToDocumentLogger(line, -1, "");
				} else if (line.contains("Network stats")) {
					mainLogger.addInfoToDocumentLogger(line, -1, "");
				}
				if ((!line.equals("")) && (!line.contains("Send"))) {
					Logger.getLogger(this.getClass()).debug("Monkey log: " + line);
					lastLine = line;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(
					"exception while communicating with monkey:" + e.getMessage());
			return false;
		} finally {
			if (lastLine.contains("Monkey finished"))
				result = true;
			else {
				Logger.getLogger(this.getClass()).debug("Error in Monkey : " + lastLine);

				String error = ResourceManager.getInstance().getString("ANDROID_RANDOM_FAILED")
						+ " " + lastLine;
				ErrorManager.getInstance().addError(getClass().getName(), error);
				throw new PhoneException(error);
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e1) {
				Logger.getLogger(this.getClass()).error(
						"exception while communicating with monkey:" + e1.getMessage());
			}
		}

		return result;

	}

	public void disableUSBcharge() throws PhoneException {
		// TODO Auto-generated method stub
	}

	public void freeStorage() throws PhoneException {
		// TODO Auto-generated method stub
	}

	public void fillStorage(long fillSpace) throws PhoneException {
		// permission denied
		long percentage = -1;

		Pattern pat = Pattern.compile("/data:.*total,.*used, ([0-9]*)K available.*");
		Matcher mtc;
		long freeDataSpace = 1;
		for (String resultat : executeShellCommand("df /data", true)) {
			mtc = pat.matcher(resultat);
			if (mtc.matches())
				freeDataSpace = Long.parseLong(mtc.group(1));
		}

		percentage = fillSpace * 100 / freeDataSpace;

		executeShellCommand("fillup -p " + Long.toString(percentage), false);
	}

	public String getCurrentMidlet() throws PhoneException {
		// TODO Auto-generated method stub
		return null;
	}

	private void openSocket(int port) throws UnknownHostException, IOException {
		/*
		 * if(listSocket==null){ listSocket = new ArrayList<Socket>(); }
		 * 
		 * for(Socket socket : listSocket){ if(port == socket.getPort()){
		 * if(socket.isConnected()) { return; } else {
		 * listSocket.remove(socket); break; } } }
		 */
		if (socket == null || socket.isClosed()) {
			Logger.getLogger(this.getClass()).info("connecting to ATKMonitor");
			try {
				socket = new Socket("127.0.0.1", port);
				// listSocket.add(socket);
				outMonitor = new PrintWriter(socket.getOutputStream(), true);
				inMonitor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (UnknownHostException e) {
				Logger.getLogger(this.getClass()).error("Unknown host: 127.0.0.1");
				throw e;
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("No I/O");
				throw e;
			}
		}

	}

	private void clearSockets() {
		// Close all opened socket to avoid problems.

		// if(null!=listSocket){
		// for(Socket socket : listSocket){
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("unable to close socket: " + e);
			}
		}
		// }
		// listSocket.clear();
		// }
		if (outMonitor != null) {
			outMonitor.close();
			outMonitor = null;
		}
		if (inMonitor != null)
			try {
				inMonitor.close();
				inMonitor = null;
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("unable to close BufferedReader");
			}

	}

	public HashMap<String, Long> getResources(List<String> resourcesName) throws PhoneException {
		// Logger.getLogger(this.getClass() ).debug("getResources");
		HashMap<String, Long> h = new HashMap<String, Long>();

		try {
			openSocket(PORT_ATK_MONITOR);
			if (!isInitResDone) {
				this.initRes(resourcesName);
				isInitResDone = true;
			}

			outMonitor.println("RES");
			String line = "";
			String[] values;
			boolean run = true;
			do {
				line = inMonitor.readLine();
				if (line != null) {
					// Logger.getLogger(this.getClass()).debug("line = "+line);
					if (!(line.startsWith("END"))) {

						values = line.split(" ");
						if (line.startsWith("GLOBAL")) {
							h.put("Cpu", Long.valueOf(values[1]));
							h.put("Memory", Long.valueOf(values[2]));
							h.put("Storage", Long.valueOf(values[3]));
							if (!values[5].startsWith("-"))
								h.put("SDCard", Long.valueOf(values[4]));
							h.put("Battery", Long.valueOf(values[5]));
							h.put("Data received", Long.valueOf(values[6]));
							h.put("Data sent", Long.valueOf(values[7]));
						} else {
							if (!values[1].startsWith("-"))
								h.put("Cpu_" + values[0], Long.valueOf(values[1]));
							if (!values[2].startsWith("-"))
								h.put("Memory_" + values[0], Long.valueOf(values[2]));
							if (!values[3].startsWith("-"))
								h.put("Data sent_" + values[0], Long.valueOf(values[3]));
							if (!values[4].startsWith("-"))
								h.put("Data received_" + values[0], Long.valueOf(values[4]));
						}
					} else {
						// Logger.getLogger(this.getClass()).debug("end of test");
						run = false;
					}
				} else {
					Logger.getLogger(this.getClass()).debug("line is null");
					// run=false;
					String error = ResourceManager.getInstance().getString(
							"RESOURCE_ATK_MONITOR_ERROR");
					throw new PhoneException(error);
				}
			} while (run);

		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("RESOURCE_ATK_MONITOR_ERROR");
			// ErrorManager.getInstance().addError(getClass().getName(), error,
			// e);
			throw new PhoneException(error);
		}
		return h;
	}

	private void initRes(List<String> listRes) throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Resource monitoring initialization");
		String command = null;
		try {
			// Init process monitoring
			command = "INIT ";
			List<String> listProcess = new LinkedList<String>();
			for (int i = 0; i < listRes.size(); i++) {
				String[] n = listRes.get(i).split("_");
				if ((n[0].equals("Cpu") || n[0].equals("Memory") || n[0].equals("Data sent") || n[0]
						.equals("Data received"))
						&& n.length > 1) {
					// We check that the process is not already in the list
					// to avoid to initialize twice the same process
					boolean alreadypresent = false;
					for (int j = 0; j < listProcess.size(); j++) {
						if (listProcess.get(j).equals(n[1])) {
							alreadypresent = true;
							break;
						}
					}
					if (!alreadypresent) {
						command += n[1] + " ";
						listProcess.add(n[1]);
					}
				}
			}
			// if (!command.equals("INIT ")) {
			Logger.getLogger(this.getClass()).debug(command);
			outMonitor.println(command);
			String line = inMonitor.readLine();
			// Logger.getLogger(this.getClass()).debug("line = "+line);
			if (line.startsWith("Init")) {
				Logger.getLogger(this.getClass()).debug("INIT OK");
			}
			// } else
			// Logger.getLogger(this.getClass()).debug("INIT OK - No process to monitor");
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).debug("INIT KO. The socket is probably closed.");
			throw new PhoneException("ATK Monitor - resource monitoring initialization failed");
		}

	}

	private int checkMonitor() {
		return checkPackage("com.orange.atk.monitor");
	}

	private void installATKMonitor() throws PhoneException {
		installApk(Platform.getInstance().getJATKPath() + Platform.FILE_SEPARATOR + "AndroidTools"
				+ Platform.FILE_SEPARATOR + "ATKMonitor.apk");

	}

	public int checkARODataCollector() {
		return checkPackage("com.att.android.arodatacollector");
	}

	public void installARODataCollector() throws PhoneException {
		installApk(ARO_APK_PATH);
	}

	private int checkPackage(String packagename) {
		String startCmd = "pm list packages";
		try {
			String[] result = executeShellCommand(startCmd, true);
			for (String res : result) {
				Logger.getLogger(this.getClass()).debug(res);
				if (res.contains(packagename)) {
					return 0;
				}
			}
		} catch (PhoneException e) {
			Logger.getLogger(this.getClass()).debug("unable to check " + packagename);
		}
		Logger.getLogger(this.getClass()).debug(packagename + " not found");
		return -1;
	}

	private void installApk(String filepath) throws PhoneException {
		String filename = new File(filepath).getName();
		Logger.getLogger(this.getClass()).debug("Pushing " + filename + " on the device");
		try {
			String result = adevice.installPackage(filepath, true);
			if (result != null) {
				Logger.getLogger(this.getClass()).debug("Result of the push: " + result);
			}
		} catch (InstallException e) {
			throw new PhoneException("unable to install " + filepath);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).debug("Unable to wait install termination");
		}
	}

	public void sendEmail(String Subject, String Msg, String EmailDest, String NameDest,
			String NameSrc, String EmailSrc) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void sendSMS(String PhoneNumber, String Msg) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void setOrientation(int direction) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void setSleepMode(boolean issleep) {
		// TODO Auto-generated method stub

	}

	public void startTestingMode() throws PhoneException {
		isStarted = true;
		String command = null;
		isInitResDone = false;
		boolean pushATK = false;
		// check Devices and if adb is know command
		if (adevice == null || adevice.isOffline()) {
			Logger.getLogger(this.getClass()).debug("Can't Detect Device");
			return;
		}
		// AutomaticPhoneDetection.getInstance().pauseDetection();
		clearSockets();

		// Forward tcp port
		try {
			adevice.createForward(PORT_ATK_MONITOR, PORT_ATK_MONITOR);
		} catch (TimeoutException e1) {
			Logger.getLogger(this.getClass()).error("Timeout while setting port forwarding");
			throw new PhoneException("Can not communicate with ATK Monitor");
		} catch (AdbCommandRejectedException e1) {
			Logger.getLogger(this.getClass()).error(
					e1.getMessage() + " while setting port forwarding");
			throw new PhoneException("Can not communicate with ATK Monitor");
		} catch (IOException e1) {
			Logger.getLogger(this.getClass()).error(
					e1.getMessage() + " while setting port forwarding");
			throw new PhoneException("Can not communicate with ATK Monitor");
		}
		Logger.getLogger(this.getClass()).debug("adb forward done for port " + PORT_ATK_MONITOR);

		String line = "NA";
		if (checkMonitor() != 0) {
			Logger.getLogger(this.getClass()).info("Could not find ATKMonitor");
			installATKMonitor();
		}
		try {
			Logger.getLogger(this.getClass()).info("Starting ATKMonitor");
			String startCmd = "am broadcast -a com.orange.atk.monitor.MONITORSTARTUP";
			float version = Float.valueOf(adevice.getProperty("ro.build.version.release")
					.substring(0, 3));
			if (version >= 3.1)
				startCmd += " -f 32";
			executeShellCommand(startCmd, true);
			// check if the correct version of ATK Monitor is installed
			command = "VERSION";
			Logger.getLogger(this.getClass()).debug(command);
			try {
				openSocket(PORT_ATK_MONITOR);
				outMonitor.println(command);
				line = inMonitor.readLine();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error("Unable to connect to ATKMonitor");
			}
			Logger.getLogger(this.getClass()).info("ATKMonitor version is: " + line);
			if (!line.equals(AndroidPhone.ATK_MONITOR_VERSION)) {
				Logger.getLogger(this.getClass()).info(
						"Updating ATKMonitor to version " + AndroidPhone.ATK_MONITOR_VERSION);
				installATKMonitor();
				if (version >= 3.1)
					startCmd += " -f 32";
				executeShellCommand(startCmd, true);
			}
		} catch (PhoneException e) {
			Logger.getLogger(this.getClass()).error("unable to start monitor");
		}
		boolean useNetworkMonitor = Boolean.valueOf(Configuration.getProperty(
				Configuration.NETWORKMONITOR, "false"));
		if (useNetworkMonitor && noTcpDumpLaunch == false) {
			boolean checkRoot = isDeviceRooted();
			Logger.getLogger(this.getClass()).debug("Is phone rooted : " + checkRoot);
			if (checkRoot) {
				// Push tcpdump to device
				pushTcpdump();
				// start tcpdump on device
				tcpdumpThread = new TcpdumpThread();
				tcpdumpThread.start();
			}
		}
		Logger.getLogger(this.getClass()).debug("Testing Mode started ...");

		return;
	}

	public boolean isDeviceRooted() {
		boolean isRooted = false;
		String commandToTest = "su -c 'pwd'";
		Logger.getLogger(this.getClass()).debug("Executing : " + commandToTest);
		try {
			String[] results = executeShellCommand(commandToTest, true);
			if (results[0].contains("/")) {
				isRooted = true;
				String chmodCommand = "su -c 'chmod 444 /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq'";
				executeShellCommand(chmodCommand, false);
				String chmodCommand2 = "su -c 'chmod 444 /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq'";
				executeShellCommand(chmodCommand2, false);
			}
		} catch (PhoneException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(e);
		}
		return isRooted;
	}

	/**
	 * Push tcpdump to device
	 * 
	 * @throws PhoneException
	 */
	public void pushTcpdump() throws PhoneException {
		String tcpdumpPath = Platform.getInstance().getJATKPath() + Platform.FILE_SEPARATOR
				+ "AndroidTools" + Platform.FILE_SEPARATOR + "tcpdump";

		CommandExecutor cmd_exe = new CommandExecutor();
		String cmd = "adb -s " + uid + " push \"" + tcpdumpPath + "\" /sdcard/";
		String cmdResult = cmd_exe.execute(cmd);
		Logger.getLogger(this.getClass()).debug(
				"Results of the command : " + cmd + "\n" + cmdResult);
		cmd = "su -c 'cat /sdcard/tcpdump > /data/local/tcpdump'";
		String results[] = executeShellCommand(cmd, true);
		Logger.getLogger(this.getClass()).debug(
				"Results of the command : " + cmd + "\n" + results[0]);
		// make tcpdump executable
		String commandToTest = "su -c 'chmod 777 /data/local/tcpdump'";
		executeShellCommand(commandToTest, false);
	}

	public void removeTcpdumpFromDevice() throws PhoneException {
		String commandToTest = "rm /data/local/tcpdump";
		executeShellCommand(commandToTest, false);
	}

	private TcpdumpThread tcpdumpThread;

	/**
	 * List of tcpdump listeners
	 */
	private Vector<TcpdumpLineListener> tcpdumpListeners = new Vector<TcpdumpLineListener>();

	/**
	 * Set to true if you dont need tcpdump before using startTestingMode
	 */
	private boolean noTcpDumpLaunch = false;

	/**
	 * Launch tcpdump on the device
	 */
	private class TcpdumpThread extends Thread {

		@Override
		public void run() {
			super.run();
			// start tcpdump
			executeTcpdumpShellCommand();
		}

		/**
		 * 
		 * @param cmd
		 *            The shell command which will be executed by Android.
		 * @param output
		 *            . set it to True if you want the output of the command
		 * 
		 * @return The output of the shell command (line by line)
		 */
		protected void executeTcpdumpShellCommand() {
			// Tcpdump, show headers
			// only get lines starting with "xx:xx" and followed by "Host:"
			// results go to stdout
			DdmPreferences.setTimeOut(30000000);
			String commandToTest = "su -c '/data/local/tcpdump -A -s 500 '";
			IShellOutputReceiver receiver;

			Logger.getLogger(this.getClass()).debug("executeShellCommand: " + commandToTest);

			receiver = new MultiLineReceiver() {

				public boolean isCancelled() {
					return false;
				}

				@Override
				public void processNewLines(String[] lines) {
					String latesttimestamp = "", host = "";
					for (String line : lines) {
						if (line.matches("\\b\\d{2}:\\d{2}:\\d{2}\\.\\d{6} .*")) {
							latesttimestamp = line.split(" ")[0];
						}
						if (line.startsWith("Host")) {
							host = line.split(":")[1];
							line = latesttimestamp + host;
							Logger.getLogger(this.getClass()).debug("Line=" + line);
							for (Iterator<TcpdumpLineListener> iterator = tcpdumpListeners
									.iterator(); iterator.hasNext();) {
								TcpdumpLineListener listener = (TcpdumpLineListener) iterator
										.next();
								listener.newTcpDumpLine(line);
							}
						}
					}
				}
			};

			try {
				if (adevice != null) {
					adevice.executeShellCommand(commandToTest, receiver);
				}
			} catch (IOException e) {
				ErrorManager.getInstance().addError(getClass().getName(),
						ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			} catch (TimeoutException e) {
				ErrorManager.getInstance().addError(getClass().getName(),
						ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			} catch (AdbCommandRejectedException e) {
				ErrorManager.getInstance().addError(getClass().getName(),
						ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			} catch (ShellCommandUnresponsiveException e) {
				ErrorManager.getInstance().addError(getClass().getName(),
						ResourceManager.getInstance().getString("SCRIPT_COMMAND_FAILURE"), e);
			}
		}

		@Override
		public void interrupt() {
			super.interrupt();
			try {
				if (tcpdumpThread != null) {
					killTcmdump();
				}
			} catch (PhoneException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
	}

	/**
	 * Kill running tcpdump on device
	 * 
	 * @throws PhoneException
	 */
	public void killTcmdump() throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Killing tcpdump");
		String commandToTest = "ps";
		String results[] = executeShellCommand(commandToTest, true);
		for (String r : results) {
			if (r.contains("tcpdump")) {
				Logger.getLogger(this.getClass()).debug(r);
				String pid = r.split("\\s+")[1];
				Logger.getLogger(this.getClass()).debug("tcpdump pid=" + pid);

				commandToTest = "su -c 'kill -9 " + pid + "'";
				Logger.getLogger(this.getClass()).debug(commandToTest);
				executeShellCommand(commandToTest, false);
			}
		}

	}

	public void stopTestingMode() {
		isStarted = false;
		try {
			if (inMonitor != null)
				inMonitor.close();
			if (outMonitor != null)
				outMonitor.close();
		} catch (IOException e1) {
			Logger.getLogger(this.getClass()).error(e1);
		}
		if (tcpdumpThread != null) {
			tcpdumpThread.interrupt();
			try {
				tcpdumpThread.join();
				// Clear listeners
				tcpdumpListeners.clear();
				removeTcpdumpFromDevice();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error(e);
			} catch (PhoneException e) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		Logger.getLogger(this.getClass()).debug("End of Testing Mode");
		// AutomaticPhoneDetection.getInstance().resumeDetection();

	}

	public void useCpu(int percentUse) throws PhoneException {
		// TODO Auto-generated method stub

	}

	/**
	 * add a new Standart Output in log file
	 * 
	 * @param Stdoutput
	 *            Standart Output
	 * @throws PhoneException
	 */

	public void fireStdOutput(String stdoutput) {
		for (IMeasureListener listener : getPerfListeners()) {
			if (listener != null)
				listener.StdOutputChangee(stdoutput);

		}
	}

	/**
	 * add a new Asynchronous Measurement (Float)
	 * 
	 * @param newValue
	 *            New Measurement
	 * @param key
	 *            name of Measurement in config file xml
	 */

	public void fireFloatValue(float newValue, String key) {
		for (IMeasureListener listener : getPerfListeners()) {
			if (listener != null)
				listener.FloatValueChangee(newValue, key);

		}

	}

	/**
	 * add a new Asynchronous Measurement (Long)
	 * 
	 * @param newValue
	 *            New Measurement
	 * @param key
	 *            name of Measurement in config file xml
	 */
	public void fireLongValue(long newMemValue, String key) {
		for (IMeasureListener listener : getPerfListeners()) {
			if (listener != null)
				listener.LongValueChangee(newMemValue, key);
		}
	}

	public void addPerfListener(IMeasureListener listener) {
		listeners.add(IMeasureListener.class, listener);
	}

	public void removePerfListener(IMeasureListener listener) {
		listeners.remove(IMeasureListener.class, listener);
	}

	public IMeasureListener[] getPerfListeners() {
		return listeners.getListeners(IMeasureListener.class);
	}

	public void setvariable(String testFile, String outputDir) {
	}

	public void touchScreenDragnDrop(List<Position> path) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void touchScreenPress(Position click) throws PhoneException {
		int x = click.getX();
		int y = click.getY();
		executeShellCommand("input motionevent " + x + " " + y, false);
	}

	public void touchScreenSlide(List<Position> path) throws PhoneException {
		if (path == null || path.size() < 2)
			throw new PhoneException("No enough coordiante in path.");
		int xorigin = path.get(0).getX();
		int yorigin = path.get(0).getY();

		int xtarget = path.get(path.size() - 1).getX();
		int ytarget = path.get(path.size() - 1).getY();

		executeShellCommand("input slide " + xorigin + " " + yorigin + " " + xtarget + " "
				+ ytarget, false);
	}

	public String[] getRandomTestList() {
		try {
			if (!isStarted)
				startTestingMode();
		} catch (PhoneException e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(e1);
		}
		try {
			openSocket(PORT_ATK_MONITOR);
			outMonitor.println("RANDOMLIST");
			String line = "";
			int nbProcess = new Integer(inMonitor.readLine()).intValue();
			String[] processList = new String[nbProcess];
			for (int i = 0; i < nbProcess; i++) {
				line = inMonitor.readLine();
				// Logger.getLogger(this.getClass()).debug("line = "+line);
				processList[i] = line;
			}
			java.util.Arrays.sort(processList);
			return processList;
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("RANDOMLIST_ATK_MONITOR_ERROR");
			ErrorManager.getInstance().addError(getClass().getName(), error, e);
		}
		return null;

	}

	public Hashtable<String, String> getProcessInfo() {
		try {
			if (!isStarted)
				startTestingMode();
		} catch (PhoneException e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(e1);
		}
		try {
			openSocket(PORT_ATK_MONITOR);
			outMonitor.println("PROCESSINFO");
			String line = "";
			int nbProcess = new Integer(inMonitor.readLine()).intValue();
			Hashtable<String, String> processInfo = new Hashtable<String, String>();
			String[] values;
			for (int i = 0; i < nbProcess; i++) {
				line = inMonitor.readLine();
				// Logger.getLogger(this.getClass()).debug("line = "+line);
				values = line.split(" ");
				String packages = processInfo.get(values[0]);
				if (packages == null)
					processInfo.put(values[0], values[1]);
				else
					processInfo.put(values[0], packages + "," + values[1]);
			}
			return processInfo;
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("RANDOMLIST_ATK_MONITOR_ERROR");
			ErrorManager.getInstance().addError(getClass().getName(), error, e);
		}
		return null;

	}

	public String[] getMonitorList() {
		try {
			if (!isStarted) {
				noTcpDumpLaunch = true;
				startTestingMode();
				noTcpDumpLaunch = false;
			}
		} catch (PhoneException e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(e1);
		}
		try {
			openSocket(PORT_ATK_MONITOR);
			outMonitor.println("PROCESSLIST");
			String line = "";
			int nbProcess = new Integer(inMonitor.readLine()).intValue();
			String[] processList = new String[nbProcess];
			for (int i = 0; i < nbProcess; i++) {
				line = inMonitor.readLine();
				// Logger.getLogger(this.getClass()).debug("line = "+line);
				processList[i] = line;
			}
			java.util.Arrays.sort(processList);
			stopTestingMode();
			return processList;
		} catch (Exception e) {
			String error = ResourceManager.getInstance().getString("RANDOMLIST_ATK_MONITOR_ERROR");
			ErrorManager.getInstance().addError(getClass().getName(), error, e);
		}
		stopTestingMode();
		return null;
	}

	public void stopRecordingMode() {
		// TODO Auto-generated method stub

	}

	protected void phoneKeyPressed(String key) {
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.phoneKeyPressed(key);
	}

	protected void phoneKeyReleased(String key) {
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.phoneKeyReleased(key);
	}

	protected void phoneKey(String key, int keyPressTime, int delay) {
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.keyPress(key, keyPressTime, delay);
	}

	protected void phoneTouchScreenPressed(int x, int y, long time) {
		Position click = new Position(x, y, time);
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.touchScreenPressed(click);
	}

	protected void phoneTouchScreenSlide(List<Position> path) {
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.touchScreenSlide(path);
	}

	protected void phoneTouchScreenDragndrop(List<Position> path) {
		for (IPhoneKeyListener listener : getKeyListeners())
			listener.touchScreenDragnDrop(path);
	}

	public void addPhoneKeyListener(IPhoneKeyListener listener) {
		listeners.add(IPhoneKeyListener.class, listener);
	}

	public void removePhoneKeyListener(IPhoneKeyListener listener) {
		listeners.remove(IPhoneKeyListener.class, listener);
	}

	public IPhoneKeyListener[] getKeyListeners() {
		return listeners.getListeners(IPhoneKeyListener.class);
	}

	public HashMap<String, String> getKeys() {
		HashMap<String, String> result = new HashMap<String, String>();
		Set<String> keys = keysAssociations.keySet();
		for (String el : keys)
			result.put(el, keysAssociations.get(el).iconpath);
		return result;
	}

	public String[] getKeyLayouts() {
		return new String[]{"ANDROID_NAVIGATION", "QWERTY"};
	}

	public String[] getRecordPhoneMode() {
		String[] phonemode = {"Phone"};
		// String [] phonemode ={ "Phone", "Emulator"};
		return phonemode;
	}

	public void mouseDown(int x, int y) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public void mouseUp(int x, int y) throws PhoneException {
		// TODO Auto-generated method stub

	}

	public String getName() {
		if (name == null) {
			name = AndroidPhone.getName(adevice);
		}
		return name;
	}

	public String getUID() {
		if (uid == null)
			uid = AndroidPhone.getUID(adevice);
		return uid;
	}

	public static String getUID(IDevice device) {
		String uidAndroid = device.getSerialNumber();
		if (uidAndroid != null)
			return uidAndroid;
		return "";
	}

	public static String getName(IDevice device) {
		String nameAndroid = "Android";
		if (device.isEmulator()) {
			String version = device.getProperty("ro.build.version.release");
			if (version != null)
				nameAndroid += " v" + version;
		} else {
			String vendor = getVendor(device);
			String model = getModel(device);
			if (vendor != null) {
				if (model != null)
					nameAndroid += " " + vendor + " " + model;
				else
					return nameAndroid += " " + vendor;
			} else if (model != null)
				nameAndroid += " " + model;
		}
		return nameAndroid;
	}

	public static String getVendor(IDevice device) {
		int times = 0;
		String vendor = device.getProperty("ro.product.manufacturer");
		while (vendor == null && times < 5) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Logger.getLogger(AndroidPhone.class.getName()).error(e);
			}
			vendor = device.getProperty("ro.product.manufacturer");
			times++;
		}
		return vendor;
	}

	public static String getModel(IDevice device) {
		int times = 0;
		String model = device.getProperty("ro.build.product");
		while (model == null && times < 5) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Logger.getLogger(AndroidPhone.class.getName()).error(e);
			}
			model = device.getProperty("ro.build.product");
			times++;
		}
		return model;
	}

	public static String getVersion(IDevice device) {
		String version = device.getProperty("ro.build.version.release");
		return version;
	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean failed) {
		isFailed = failed;
	}

	public int getCnxStatus() {
		return cnxStatus;
	}

	public void setCnxStatus(int status) {
		cnxStatus = status;
	}

	public void startRecordingMode() throws PhoneException {
		// This method must be override
		return;
	}

	public boolean isInRecordingMode() {
		return isScriptRecording;
	}

	public boolean isInTestingMode() {
		return isStarted;
	}

	public void addTcpdumpLineListener(TcpdumpLineListener listener) {
		Logger.getLogger(this.getClass()).debug("Adding tcpdump listener");
		tcpdumpListeners.add(listener);
	}

	public int getType() {
		return PhoneInterface.TYPE_ANDROID;
	}

	public String getIncludeDir() {
		return "\\includeAndroid";
	}

	public String getConfigFile() {
		return "android.xml";
	}

	//add Robotium task
	protected String  [] executeShellCommand(String cmd) throws PhoneException{
		IShellOutputReceiver receiver;
		list =new ArrayList<String>();
		receiver = shellOutputReceiver;

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
			return null;
	}

	@Override
	public void sendCommandToExecuteToSolo(Object[] commands) throws PhoneException {
		robotiumTask.sendCommandToExecuteToSolo(commands);
	}

	@Override
	public void setApkToTestWithRobotiumParam(String packName,
			String activityName, String packsourceDir, int versionCode)
			throws PhoneException {
		robotiumTask.setApkToTestWithRobotiumParam(packName, activityName, packsourceDir, versionCode);
	}

	@Override
	public ArrayList<String> getAllInstalledAPK() throws PhoneException {
		return robotiumTask.getAllInstalledAPK();
	}

	@Override
	public ArrayList<String> getForegroundApp() throws PhoneException {
		return robotiumTask.getForegroundApp();
	}

}
