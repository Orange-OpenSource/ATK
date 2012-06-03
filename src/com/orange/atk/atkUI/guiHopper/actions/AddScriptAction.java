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
 * File Name   : AddScriptAction.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.atkUI.guiHopper.HopperNewStepDialog;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class AddScriptAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 * @param icon
	 * @param shortDescription
	 */
	public AddScriptAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if(AutomaticPhoneDetection.getInstance().
				getDevice() instanceof DefaultPhone)
		{ 
			JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "Can't Detect device");
			return;
		}else if(AutomaticPhoneDetection.getInstance().
				getDevice().getCnxStatus()!=PhoneInterface.CNX_STATUS_AVAILABLE)
		{
			JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "Can't Detect device");
			return;
		}
		new HopperNewStepDialog();
	}

}
