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
 * File Name   : AddCheckListAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.tasks.LoadCheckListTask;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class AddCheckListAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;
	private String lastChkLstFilePath = null;

	public AddCheckListAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
		lastChkLstFilePath = getDefaultPath();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		JFileChooser fileChooser = new JFileChooser(lastChkLstFilePath);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileUtilities.Filter("MCL file [*.mcl]", ".mcl"));
		int returnVal = fileChooser.showDialog(matosGui, "Load a check-list");
		String src = "";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			src = fileChooser.getSelectedFile().getAbsolutePath();
			src = FileUtilities.verifyExtension(src, ".mcl");
			if (src==null || src.equals("")) {
				JOptionPane.showMessageDialog(
						matosGui,
						"You must indicate the file which contains the check-list description.",
						"Error !",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					Campaign camp = Campaign.readCampaign(new File(src));
					matosGui.setModified(true);
					new LoadCheckListTask(matosGui.statusBar, camp, -1 /* ie at the end*/, false, camp.size());
					lastChkLstFilePath = src;
					matosGui.setCheckListFileName(src);
				} catch (Alert a) {
					JOptionPane.showMessageDialog(
							matosGui,
							a.getMessage(),
							"Error !",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private String getDefaultPath(){
		try {
			String path = Configuration.getProperty(Configuration.INPUTDIRECTORY);
			Logger.getLogger(this.getClass() ).debug("InputDir="+path);
			return path;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass() ).debug(" no input dir set in configuration file [ConfigurtionDialog]");
			return null;
		}
	}

}
