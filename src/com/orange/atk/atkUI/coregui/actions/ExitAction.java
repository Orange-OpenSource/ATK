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
 * File Name   : ExitAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;

/**
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class ExitAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 * @param icon
	 * @param short description
	 */
	public ExitAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		// remember last dimension & location
		Configuration.setProperty(Configuration.GUI_WIDTH, "" + matosGui.getWidth());
		Configuration.setProperty(Configuration.GUI_HEIGTH, "" + matosGui.getHeight());
		Configuration.setProperty(Configuration.GUI_LOCATION_X, "" + matosGui.getLocation().x);
		Configuration.setProperty(Configuration.GUI_LOCATION_Y, "" + matosGui.getLocation().y);

		if (matosGui.isModified() && matosGui.getStepNumber() > 0) {
			// ask for saving checklist
			int n = JOptionPane.showConfirmDialog(matosGui,
					"The current check-list is not saved, do you want to save it? ",
					"Checklist not saved", JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				MatosAction.SAVEALLALL.getAction().actionPerformed(e);
			} else
				if (n == JOptionPane.NO_OPTION) {
					// nothing more to do here
				} else { // CANCEL
					return;
				}
		}

		try {
			Configuration.writeProperties();
		} catch (Alert e1) {
			Logger.getLogger(this.getClass()).error(e1);
		}
		Logger.getLogger(this.getClass()).error("Stopping application...");

		System.exit(0);

	}

}
