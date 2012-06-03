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
 * File Name   : KeyboardChannelPanel.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import java.util.Hashtable;

import javax.swing.ProgressMonitor;

import com.android.ddmlib.IDevice;
import com.orange.atk.phone.PhoneException;

public class KeyboardChannelPanel extends ChannelPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int keyboardNb;
	protected RegisterKeysPanel registerKeysPanel;
	ProgressMonitor progressBar;
	
	
	public KeyboardChannelPanel(int keyboardNb, AndroidWizard wizard, IDevice device, Hashtable<String,String> detectedChannels) throws PhoneException {
		super(wizard,device,detectedChannels);
		this.keyboardNb = keyboardNb;
		if (keyboardNb==1) infoLabel1.setText("Select a channel and press MENU key on your phone.");
		else infoLabel1.setText("Select another channel and press an unregistered key on your phone.");
		infoLabel2.setText("If phone screen icon becomes blue when pressing the key, then validate keyboard channel.");
		infoLabel3.setText("If phone screen icon is always blue or always red, then select another channel and retry.");

	}

	public void validateMethod() {
		if (recordingThread != null) recordingThread.stoprecording();
		progressBar = new ProgressMonitor(this, null,"",0,100);
		progressBar.setNote("Please wait ...");
		progressBar.setMillisToDecideToPopup(0);
		progressBar.setMillisToPopup(0);
		validateButton.setEnabled(false);
		progressBar.setProgress(5);
		wizard.repaint();
		Thread t = new Thread() {
			  public void run() {
				  wizard.setKeyboard(keyboardNb, (String) channelsCombo.getSelectedItem());
				  if (recordingThread != null) recordingThread.stoprecording();
				  if (registerKeysPanel == null) {
					  try {
						  progressBar.setProgress(10);
						  registerKeysPanel = new RegisterKeysPanel(keyboardNb, wizard, device, detectedChannels, progressBar);
						  progressBar.setProgress(90);
					  } catch (PhoneException e) {
						e.printStackTrace();
					  }
					  wizard.addStep(registerKeysPanel,"Press the keys for Keyboard "+keyboardNb);
				  } else wizard.nextStep();
				  progressBar.setProgress(100);
				  validateButton.setEnabled(true);
			  }
		};
		t.start();
	}

}
