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
 * File Name   : KeyboardEventfilter.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.android.EventFilter;

/**
 * Class used to filter output of very common keyboard,
 * 
 * It's found on HTC magic and G1, Samsung SPICA , MOtorola Morisson...
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class KeyboardEventfilter extends EventFilter implements TimeOutListener {
	
	private RegisterKeysPanel registerKeysPanel;
	//Used for ATK Wizard
	private PrintWriter outWizard;
	private BufferedReader inWizard;
	private Socket socket;
	private AndroidWizard wizard;
	private String patternX;
	private String patternY;
	private EventTimerThread eventTimerThread;
	private String keyName = "NULL";
	private double avgX = 0;
	private double avgY = 0;
	private int count = 0;
	
	public KeyboardEventfilter(AndroidWizard wizard, RegisterKeysPanel registerKeysPanel, ProgressMonitor progressBar) {
		this.registerKeysPanel = registerKeysPanel;
		this.wizard = wizard;
		patternX = wizard.getPatternX();
		patternY = wizard.getPatternY();
		try {
			wizard.installATKWizard();
			progressBar.setProgress(40);
			wizard.startATKWizard();
			progressBar.setProgress(60);
		} catch (PhoneException e) {
			e.printStackTrace();
		}
		if (!getReady()) {
			registerKeysPanel.info("ATK Wizard could not be installed on the phone.");
			registerKeysPanel.abort();
		} 
		progressBar.setProgress(70);
	}

	@Override
	public void processline( String line) {
		String commands[] = line.split(": ");
		String command;
		if(commands.length>1)
			command = commands[1];
		else
			return;
		if (command.startsWith("0001 014a")) {

		} else if (command.startsWith("0001")) {
			Logger.getLogger(this.getClass()).debug("line="+command);
			if(command.substring(10).equals("00000000")) { // KEY RELEASED
				int keyCode = Integer.parseInt(command.substring(5,9),16);
				keyName = getKeyName();
				if (keyName.equals("NULL"))  this.askKeyName();
				if (!keyName.equals("NULL")) registerKeysPanel.registerKey(keyName,keyCode);
				else keyName = "NULL";
			}
		} else if (patternX!=null && command.startsWith(patternX)) {
			Logger.getLogger(this.getClass()).debug("line="+command+" avgX="+avgX);
			String name = getKeyName();
			if (!name.equals("NULL")) keyName = name;
			if (avgX == 0) {
				Logger.getLogger(this.getClass()).debug("starting timer");
				eventTimerThread = new EventTimerThread(this, 300);
				eventTimerThread.start();
			} else eventTimerThread.newEventTime(System.currentTimeMillis());
			avgX = ((avgX * count) + Integer.parseInt(command.substring(10), 16)) / (count+1);
			count++;
			
		} else if (patternY!=null && command.startsWith(patternY)) {
			Logger.getLogger(this.getClass()).debug("line="+command);
			String name = getKeyName();
			if (!name.equals("NULL")) keyName = name;
			avgY = ((avgY * count) + Integer.parseInt(command.substring(10), 16)) / (count+1);
		}
			
	}
	
	private String getKeyName() {
		String line = "UNKNOWN";
		outWizard.println("KEYNAME");
		try {
			line = inWizard.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	private boolean getReady() {
		Logger.getLogger(this.getClass()).debug("Trying to get ready ...");
		try{
			socket = wizard.getWizardSocket();
			if(socket!=null){
				outWizard = new PrintWriter(socket.getOutputStream(), true);
				inWizard = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			}
			//check if ATK Wizard is installed
			outWizard.println("READY");
			String line = inWizard.readLine();
			Logger.getLogger(this.getClass()).debug("READY = "+line);
			if (line.equals("OK")) {
				outWizard.println("DISPLAY");
				line = inWizard.readLine();
				Logger.getLogger(this.getClass()).debug("width = "+line);
				wizard.setScreenWidth(Integer.parseInt(line));
				line = inWizard.readLine();
				Logger.getLogger(this.getClass()).debug("height = "+line);
				wizard.setScreenHeight(Integer.parseInt(line));				
				return true;
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).debug("Check ATK Wizard Ready failed.");
		}
		return false;
	}

	public void notifyTimeOut() {
		Logger.getLogger(this.getClass()).debug("stop timer");
		eventTimerThread.stopRunning();
		if (keyName.equals("NULL"))  askKeyName();
		if (!keyName.equals("NULL")) registerKeysPanel.registerKey(keyName, (int) (avgX / wizard.getRatioX()), (int) (avgY / wizard.getRatioY()));
		keyName="NULL";
		avgX = 0;
		avgY = 0;
		count = 0;
	}
	
	private void askKeyName() {
		Object ret = JOptionPane.showInputDialog(null,"This special key can't be catched by ATK, please indicate its name manually :"
				, "Indicate the key name", JOptionPane.QUESTION_MESSAGE, null, KeyEventNames.keyEventNames.toArray(), KeyEventNames.keyEventNames.get(0)) ;
		if (ret!=null) keyName = (String) ret;
		Thread t = new Thread() {
			  public void run() {
					try {
						wizard.startATKWizard();
					} catch (PhoneException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			  }
		};
		t.start();
	}

}