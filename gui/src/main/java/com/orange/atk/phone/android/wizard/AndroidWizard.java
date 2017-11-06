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
 * File Name   : AndroidWizard.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.android.AndroidDriver;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.Position;

public class AndroidWizard extends JFrame {
	private static final String TITLE= "Android Wizard";
	protected final static int PORT_ATK_WIZARD = 1358; 
	private String configFileName = "";
	private Hashtable<Integer,JPanel> wizardSteps = new Hashtable<Integer,JPanel>();
	private Hashtable<Integer,String> wizardStepTitle = new Hashtable<Integer,String>();
	private Hashtable<String,String> detectedChannels = new Hashtable<String,String>();
	private Hashtable<String,StringBuffer> channelEvents = new Hashtable<String,StringBuffer>();
	AndroidDriver phone;
	IDevice device;
	int currentStep = -1;


	// Android Config file parameters
	private String touchscreen = "";
	private String keyboard = "";
	private String keyboard2 = "";
	private String keyboard3 = "";
	private HashMap<String, Position> softKeyMap = new HashMap<String, Position>(); //HashMap<keyName,(X,Y)>
	private HashMap<String, Integer> keyMap = new HashMap<String, Integer>(); //HashMap<keyName,keyCode>
	private HashMap<String, String> keyCanal = new HashMap<String, String>(); // HashMap<keyName,keyboard>
	private HashMap<String, String> softKeyCanal = new HashMap<String, String>(); // HashMap<keyName,keyboard>
	private boolean ATKWizardInstalled = false;
	private Socket socket;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private int codeX = -1;
	private int maxX = 0;
	private int codeY = -1;
	private int maxY = 0;
	private String patternX;
	private String patternY;
	
	private IShellOutputReceiver shellOutputReceiver = new IShellOutputReceiver() {	
		public void addOutput(byte[] data, int offset, int length) {}
		public void flush() {}
		public boolean isCancelled() {
			return false;
		}
	};


	public AndroidWizard(AndroidDriver phone, IDevice device, String confFileName) throws PhoneException {
		super(TITLE);
		configFileName = confFileName;
		this.device = device;
		this.phone = phone;
		try {
			device.executeShellCommand("getevent -p", 
					new DetectAllChannelsEventFilter(detectedChannels, channelEvents));

		} catch (IOException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error); 
		} catch (TimeoutException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		} catch (AdbCommandRejectedException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		} catch (ShellCommandUnresponsiveException e) {
			String error = ResourceManager.getInstance().getString("ANDROID_CHANNEL_DETECTION_ERROR"); 
			ErrorManager.getInstance().addWarning(getClass().getName(), error, e); 
			throw new PhoneException(error);
		}

		addStep(new StartWizardPanel(this,phone),"Android Wizard");
		addStep(new TouchscreenChannelPanel(this,device,detectedChannels),"Select the channel for the touchscreen");
		this.setSize(600, 400);
		getContentPane().setLayout(new FlowLayout());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {}

			public void windowClosed(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				exit(false);
			}

			public void windowDeactivated(WindowEvent arg0) {}

			public void windowDeiconified(WindowEvent arg0) {}

			public void windowIconified(WindowEvent arg0) {}

			public void windowOpened(WindowEvent arg0) {}

		});

		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fSize = this.getSize();
		this.setLocation((sSize.width-fSize.width)/2, (sSize.height-fSize.height)/2);
		goToStep(0);
	}

	public void addStep(JPanel panel,String title) {
		wizardSteps.put(new Integer(currentStep+1),panel);
		wizardStepTitle.put(new Integer(currentStep+1), title);
		goToStep(currentStep+1);
	}

	public void exit(boolean force) {
		if (force) close();
		else {
			int result = JOptionPane.showConfirmDialog(this, 
					"Are you sure you want to exit wizard ? Configuration file won't be generated.", 
					"Confirmation",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				close();
			}
		}
	}

	private void close() {
		this.dispose();
		try { 
			this.uninstallATKWizard();
		} catch (PhoneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nextStep() {
		goToStep(currentStep+1);
	}

	public void prevStep() {
		goToStep(currentStep-1);
	}

	private void goToStep(int step) {
		currentStep=step;
		getContentPane().removeAll();
		if (step>0) this.setTitle(TITLE+": "+wizardStepTitle.get(new Integer(step))+" (Step "+step+")");
		if (wizardSteps.get(new Integer(step)) == null) Logger.getLogger(this.getClass()).debug("Step "+step+" is null !!");
		getContentPane().add(wizardSteps.get(new Integer(step)));
		pack();
		setVisible(true);
	}


	protected void installATKWizard() throws PhoneException {
		if (!ATKWizardInstalled) {
			Logger.getLogger(this.getClass()).debug("Installing ATK Wizard on phone");
			//push ATKMonitor to the Device	
			try {
				String result = device.uninstallPackage("com.orange.atk.wizard");
				if(result!=null){
					Logger.getLogger(this.getClass()).debug("Result of the uninstall: "+result);
				}
				device.installPackage(
						Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools"+Platform.FILE_SEPARATOR+"ATKWizard.apk", true);
			} catch (InstallException e) {
				e.printStackTrace();
				throw new PhoneException("ATK Wizard - unable to install ATK Wizard");
			}
			//Forward tcp port
			String adbLocation = Platform.getInstance().getDefaultADBLocation();
			Runtime r =Runtime.getRuntime();
			String [] args1 = {adbLocation, "forward" ,"tcp:"+PORT_ATK_WIZARD,"tcp:"+PORT_ATK_WIZARD};
			Process p;
			BufferedReader in_br=null;
			try {
				p = r.exec(args1);
				in_br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line ="";
				while ((line =in_br.readLine()) != null) {
					Logger.getLogger(this.getClass() ).debug("adb forward done : "+line);	
					if (line.contains("daemon")) {throw new PhoneException("ATK Wizard - unable to install ATK Wizard");}
				}
				in_br.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new PhoneException("ATK Wizard - unable to install ATK Wizard");
			}
			Logger.getLogger(this.getClass() ).debug("adb forward done for port "+PORT_ATK_WIZARD+"\n");

			ATKWizardInstalled = true;
		}
	}

	protected void startATKWizard() throws PhoneException {
		Logger.getLogger(this.getClass()).debug("Starting ATK Wizard");
		//run ATKWizard
		try {
			device.executeShellCommand("am start -n com.orange.atk.wizard/.ATKWizardClient",shellOutputReceiver);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PhoneException("ATK Wizard - unable to launch ATK Wizard");
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new PhoneException("ATK Wizard - unable to launch ATK Wizard");
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
			throw new PhoneException("ATK Wizard - unable to launch ATK Wizard");
		} catch (ShellCommandUnresponsiveException e) {
			e.printStackTrace();
			throw new PhoneException("ATK Wizard - unable to launch ATK Wizard");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {}
		Logger.getLogger(this.getClass()).debug("ATK Wizard is launched on the device ...");
	}

	protected void uninstallATKWizard() throws PhoneException {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new PhoneException("ATK Wizard - unable to uninstall ATK Wizard");
			}
		}
		Logger.getLogger(this.getClass()).debug("Uninstalling ATK Wizard from phone");
		try {
			String result = device.uninstallPackage("com.orange.atk.wizard");
			if(result!=null){
				Logger.getLogger(this.getClass()).debug("Result of the uninstall: "+result);
			}
		} catch (InstallException e) {
			e.printStackTrace();
			throw new PhoneException("ATK Wizard - unable to uninstall ATK Wizard");
		}
	}
	
	protected Socket getWizardSocket() throws UnknownHostException, IOException  {
		if (socket==null) socket = new Socket("127.0.0.1", PORT_ATK_WIZARD);
		return socket;
	}
	
	public void printReport() {
		String configXmlFileName = configFileName+".xml";

		Document configxml = DocumentHelper.createDocument();
		
		configxml.addComment("  "+phone.getName()+" configuration file   ");
		configxml.addComment("   Screen resolution "+screenWidth+"x"+screenHeight+"   ");
		
		Element root = configxml.addElement("Android-config");

		Element canalPattern = root.addElement("CanalPattern");
		
		//keyboards
		if(!keyboard.equals("")) printKeyMapping("keyboard", keyboard, canalPattern, root);
		if(!keyboard2.equals("")) printKeyMapping("keyboard2", keyboard2, canalPattern, root);
		if(!keyboard3.equals("")) printKeyMapping("keyboard3", keyboard3, canalPattern, root);

		Set<String> softKeySet = softKeyMap.keySet();

		Element keyMapping = root.addElement("SoftKeyMapping");
		for(String key : softKeySet){
				keyMapping.addElement("Key")
				.addAttribute( "name", key)
				.addAttribute( "avgX", ""+softKeyMap.get(key).getX())
				.addAttribute( "avgY", ""+softKeyMap.get(key).getY());
		}

		//touchscreen
		if(!touchscreen.equals("")){
			canalPattern.addElement("Pattern")
			.addAttribute( "canal", "touchscreen")
			.addAttribute( "value", touchscreen.replace("\"",""));
			Element Touchscreen = root.addElement("Touchscreen");	

			Touchscreen.addComment("!!! THIS IS JUST TOUCHSCREEN Elements TEMPLATE !!!");
			Touchscreen.addComment("!!! SEE ATK User Guide - Configuration section - and UPDATE following values !!!");
			if (codeX!=-1 && codeY!=-1) {
				Touchscreen.addComment("!!! Please check following X Y ratioX and ratioY patterns");
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "X")
				.addAttribute( "value", "3 "+codeX+" ");
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "Y")
				.addAttribute( "value", "3 "+codeY+" ");
				long ratio = maxX*100/screenWidth;
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "ratioX")
				.addAttribute( "value", String.valueOf( (double)ratio / 100.0 ));
				 ratio = maxY*100/screenHeight;
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "ratioY")
				.addAttribute( "value", String.valueOf( (double)ratio / 100.0 ));
				Touchscreen.addComment("!!! Please update following patterns");
			} else {
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "X")
				.addAttribute( "value", "0 0 ");
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "Y")
				.addAttribute( "value", "0 0 ");
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "ratioX")
				.addAttribute( "value", "1.0");
				Touchscreen.addElement("Pattern")
				.addAttribute( "name", "ratioY")
				.addAttribute( "value", "1.0");
			}
			Touchscreen.addElement("Pattern")
			.addAttribute( "name", "down")
			.addAttribute( "value", "0 0 0");
			Touchscreen.addElement("Pattern")
			.addAttribute( "name", "downmax")
			.addAttribute( "value", "0 0 0");
			Touchscreen.addElement("Pattern")
			.addAttribute( "name", "up")
			.addAttribute( "value", "0 0 0");
			Touchscreen.addElement("Pattern")
			.addAttribute( "name", "flush")
			.addAttribute( "value", "0 0 0");
			Touchscreen.addElement("Pattern")
			.addAttribute( "name", "flush2")
			.addAttribute( "value", "0 2 0");
			Touchscreen.addElement("Threshold")
			.addAttribute( "name", "move")
			.addAttribute( "value", "15");
			Touchscreen.addElement("Option")
			.addAttribute( "name", "sendMouseDownForMove")
			.addAttribute( "value", "true");
			Touchscreen.addElement("Option")
			.addAttribute( "name", "sendMouseEventFirst")
			.addAttribute( "value", "true");
			Touchscreen.addElement("Option")
			.addAttribute( "name", "useMonkeyForPress")
			.addAttribute( "value", "true");
		}

		//Write the file.
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer =null;
		try {
			writer = new XMLWriter(new FileWriter(configXmlFileName),format);
			writer.write(configxml);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Show a confirm dialog and exit the wizard
		JOptionPane.showConfirmDialog(this, 
			"A template of the config file has been created under \n"+
			configXmlFileName+"\n"+
			"See ATK User guide to configure the Touchscreen section of this template\n", 
			"Success",JOptionPane.CLOSED_OPTION);
		exit(true);

	}

	private void printKeyMapping(String canal, String value, Element canalPattern, Element root) {
		Set<String> keySet = keyCanal.keySet();

		canalPattern.addElement("Pattern")
		.addAttribute( "canal", canal)
		.addAttribute( "value", value.replace("\"",""));

		Element keyMapping = root.addElement("KeyMapping").addAttribute( "canal", canal);
		for(String key : keySet){
			if(keyCanal.get(key).equals(canal)){
				keyMapping.addElement("Key")
				.addAttribute( "name", key)
				.addAttribute( "code", ""+keyMap.get(key));
			}
		}
	}

	
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	
	public String getPatternX() {
		return patternX;
	}

	public String getPatternY() {
		return patternY;
	}
	
	public double getRatioX() {
		if (screenWidth!=0) return ((double)maxX/(double)screenWidth);
		return 0;
	}
	
	public double getRatioY() {
		if (screenHeight!=0) return ((double)maxY/(double)screenHeight);
		return 0;
	}


	/******** Config file parameters *********/

	public String getKeyboard(int keyboardNb) {
		if (keyboardNb==1)
			return keyboard;
		else if (keyboardNb==2) 
			return keyboard2;
		else if (keyboardNb==3) 
			return keyboard3;
		return keyboard;
	}

	public void setKeyboard(int keyboardNb, String keyboard) {
		if (keyboardNb==1)
			this.keyboard = keyboard;
		else if (keyboardNb==2) 
			this.keyboard2 = keyboard;
		else if (keyboardNb==3) 
			this.keyboard3 = keyboard;
	}

	public void registerKey(int keyboard ,String keyName, int keyCode) {
		String keyboardType = "keyboard";
		if (keyboard==2) keyboardType = "keyboard2";
		else if (keyboard==3) keyboardType = "keyboard3";
		keyCanal.put(keyName,keyboardType);
		keyMap.put(keyName, new Integer(keyCode));
	}

	public void registerSoftKey(int keyboard ,String keyName, int avgX, int avgY) {
		// this is soft keyboard == always touchscreen
		if (keyboard==1) this.keyboard = "";  
		else if (keyboard==2) this.keyboard2 = "";  
		else if (keyboard==3) this.keyboard3 = "";  
		softKeyMap.put(keyName, new Position(avgX, avgY, 0));
	}

	public void setTouchscreen(String touchscreen, StringBuffer traces){
		this.touchscreen = touchscreen;
		// Analyze traces to get X Y event codes
		String[] tracesEvents = traces.toString().split("\n");
		int traceX = -1;
		int traceY = -1;
		for (int i=0; i<tracesEvents.length && i<20 && traceY==-1; i++) {
			String traceEvent = tracesEvents[i];
			Logger.getLogger(this.getClass()).debug("###"+traceEvent);
			Matcher mtc = Pattern.compile("\\w*-\\w*:\\s*(\\w*)\\s*(\\w*)\\s*(\\w*)\\s*").matcher(traceEvent);
			if (mtc.matches()) {
				int code = Integer.parseInt(mtc.group(2), 16);
				Logger.getLogger(this.getClass()).debug("TRACE CODE="+code);
				if (traceX==-1) {
					traceX = code;
				} else {
					if (code == traceX+1) {
						traceY = code;
						Logger.getLogger(this.getClass()).debug("traceX="+traceX+" traceY="+traceY);
					} else traceX=code;
				}
			}
		}
		// Analyze touchscreen events to get X Y ratio templates
		String touchEventsDescription = channelEvents.get(touchscreen).toString();
		String[] touchEvents = touchEventsDescription.split("\n");
		for (int i=0; i<touchEvents.length && codeY==-1; i++) {
			String touchEvent = touchEvents[i];
			if (touchEvent.indexOf(":")!=-1)  touchEvent = touchEvent.substring(touchEvent.indexOf(":")+1);
			Logger.getLogger(this.getClass()).debug("***"+touchEvent);
			Matcher mtc = Pattern.compile("\\s*(\\d*)\\s*value\\s*(-?\\d+),\\s*min\\s*(-?\\d+),\\s*max\\s*(-?\\d+),\\s*fuzz\\s*(-?\\d+)\\s*flat\\s*(-?\\d+)\\s*").matcher(touchEvent);
			if (mtc.matches()) {
				int code = Integer.parseInt(mtc.group(1), 16);
				Logger.getLogger(this.getClass()).debug("CODE="+code);
				if (code == codeX+1 && code>0 && (traceY==-1 || traceY==code)) {
					codeY = code;
					patternY = "0003 "+mtc.group(1);
					Logger.getLogger(this.getClass()).debug("codeX="+codeX+" codeY="+codeY);
					maxY = Integer.parseInt(mtc.group(4));
				} else {
					codeX = code;
					patternX = "0003 "+mtc.group(1);
					maxX = Integer.parseInt(mtc.group(4));
				}			
			}
		}
		if (codeY==-1) codeX=-1;
	}

}
