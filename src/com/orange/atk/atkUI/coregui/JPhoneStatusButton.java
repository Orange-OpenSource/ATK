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
package com.orange.atk.atkUI.coregui;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

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

	public JPhoneStatusButton() {
		deviceDetectionFrame = DeviceDetectionFrame.getInstance();
		addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				deviceDetectionFrame.display();
		}
		});
		AutomaticPhoneDetection.getInstance().addDeviceDetectionListener(this);
		deviceSelectedChanged();
		if (!(phone instanceof DefaultPhone)) devicesConnectedChanged();
	}
	
	private void setNoPhoneConnectedStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("tango/nophone.png")));
		setText(NO_PHONE);
		repaint();		
	}
	
	private void setPhoneNotAvailableStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("tango/phonenotready.png")));
		setText(phone.getName()+" ");
		repaint();		
	}

	private void setPhoneConnectedStatus() {
		setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("tango/phone.png")));
		setText(phone.getName()+" ");
		repaint();		
	}

    private void setPhoneUnconfiguredStatus() {
        setIcon(new ImageIcon(CoreGUIPlugin.getIconURL("tango/phonenotconfigured.png")));
        setText(phone.getName()+" ");
        repaint();
    }
	
	public void devicesConnectedChanged() {
		switch(phone.getCnxStatus()){
            case PhoneInterface.CNX_STATUS_AVAILABLE:
                if(phone.getPhoneConfigFile().equals("default.xml")){
                    setPhoneUnconfiguredStatus();
                }else{
			        setPhoneConnectedStatus();
                }
                break;
            default:
			    setPhoneNotAvailableStatus();
		}
	}

	public void deviceSelectedChanged() {
		PhoneInterface newphone = AutomaticPhoneDetection.getInstance().getDevice();
		if(phone != newphone) { //something has changed
			phone = newphone;
			// clear ATK check-lists
			for (AnalysisGUICommon plugin : MatosGUI.analysisPlugins) {
				plugin.getCheckListTable().clear();
				if (CoreGUIPlugin.mainFrame !=null) {
					CoreGUIPlugin.mainFrame.setModified(true);
					CoreGUIPlugin.mainFrame.updateContentTabsTitle();
					CoreGUIPlugin.mainFrame.updateButtons();
				}
			}
			if(phone instanceof DefaultPhone) {
				setNoPhoneConnectedStatus();
			} else {
				devicesConnectedChanged();
			} 
			
		}
		
	}

}
