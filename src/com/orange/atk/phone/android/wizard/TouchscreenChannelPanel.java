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
 * File Name   : TouchscreenChannelPanel.java
 *
 * Created     : 25/08/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */
package com.orange.atk.phone.android.wizard;

import java.util.Hashtable;

import com.android.ddmlib.IDevice;
import com.orange.atk.phone.PhoneException;

public class TouchscreenChannelPanel extends ChannelPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TouchscreenChannelPanel( AndroidWizard wizard, IDevice device, Hashtable<String,String> detectedChannels) throws PhoneException {
		super(wizard,device,detectedChannels);
		infoLabel1.setText("Select a channel and touch your phone's screen.");
		infoLabel2.setText("If phone screen icon becomes blue when touching the screen, then validate touchscreen channel.");
		infoLabel3.setText("If phone screen icon is always blue or always red, then select another channel and retry.");

	}

	public void validateMethod() throws PhoneException {
		this.recordingThread.stoprecording();
		wizard.setTouchscreen((String) channelsCombo.getSelectedItem(), eventFilter.getTraces());
		wizard.addStep(new KeyboardChannelPanel(1,wizard,device,detectedChannels), "Select the channel for the keyboard 1");
	}
}
