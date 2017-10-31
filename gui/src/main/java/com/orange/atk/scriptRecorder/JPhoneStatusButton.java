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
 * File Name   : JPhoneStatusButton.java
 *
 * Created     : 08/02/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.scriptRecorder;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.deviceDetectionUI.DeviceDetectionFrame;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.phone.detection.DeviceDetectionListener;


public class JPhoneStatusButton extends JButton implements DeviceDetectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8496167894511771656L;
	private static final String NO_PHONE = ResourceManager.getInstance().getString("NO_DEVICE")+" ";
	private PhoneInterface phone;
	DeviceDetectionFrame deviceDetectionFrame;
	RecorderFrame recorder;
	
	public JPhoneStatusButton(RecorderFrame recorder) {
		this.recorder = recorder;
		deviceDetectionFrame = DeviceDetectionFrame.getInstance();
		addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				deviceDetectionFrame.display();
		}
		});
	}
	
	public void initialize() {
		deviceSelectedChanged();
		if (!(phone instanceof DefaultPhone)) devicesConnectedChanged();
	}
	
	private void setNoPhoneConnectedStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("icons/rec_nophone.PNG")));
		setText(NO_PHONE);
		recorder.disableRecorder();
		repaint();		
	}
	
	private void setPhoneNotAvailableStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("icons/rec_phonenotready.PNG")));
		setText(phone.getName()+" ");
		recorder.disableRecorder();
		repaint();		
	}

	private void setPhoneConnectedStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("icons/rec_phone.PNG")));
		setText(phone.getName());
		recorder.enableRecorder();
		repaint();		
	}

	public void devicesConnectedChanged() {
		if (phone.getCnxStatus() == PhoneInterface.CNX_STATUS_AVAILABLE) {
			setPhoneConnectedStatus();
		} else {
			setPhoneNotAvailableStatus();
		}
	}

	public void deviceSelectedChanged() {
		PhoneInterface newphone = AutomaticPhoneDetection.getInstance().getDevice();
		if(phone != newphone) { //something has changed
			phone = newphone;
			if(phone instanceof DefaultPhone) {
				setNoPhoneConnectedStatus();
			} else {
				devicesConnectedChanged();
			} 
		}
		
	}
	
}
